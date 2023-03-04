package com.example.spisokdel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private DataBase dataBase;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private EditText list_name_field;
    private SharedPreferences sharedPreferences;
    private TextView info_app;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataBase = new DataBase(this);
        listView = findViewById(R.id.task_list);
        list_name_field = findViewById(R.id.list_name_field);
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        info_app = findViewById(R.id.info_app);
        info_app.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_info_text));

        String list_name = sharedPreferences.getString("list_name", ""); // для подрузки данных из памяти телефона, чб после перезахода отображалось название списка
        // что подгружаем и что устанавливаем если ничего не нашли
        list_name_field.setText(list_name);
        loadAllTask();
        changeTextAction(); // будет отслеживать ВВОД текста
    }

    private void changeTextAction()
    {
        list_name_field.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("list_name", String.valueOf(list_name_field.getText()));
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void loadAllTask()
    {
        ArrayList<String> allTask = dataBase.getAllTask();
        if (arrayAdapter == null)
        {
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.task_list_row,
                    R.id.text_table_row, allTask); // область применения - файл дизайн - в какой ряд - что
            listView.setAdapter(arrayAdapter);
        } else
        {
            arrayAdapter.clear(); // срабатывает при повторном вызове
            arrayAdapter.addAll(allTask);
            arrayAdapter.notifyDataSetChanged(); // обновляет
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) // переписываем стандартное меню
    {
        getMenuInflater().inflate(R.menu.main_menu, menu); // заменяем стандартное
        Drawable icon = menu.getItem(0).getIcon(); // меняем иконку
        icon.mutate();
        icon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN); // цвет и режим цвета
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) // проверяем нажатие на иконку
    {
        if (item.getItemId() == R.id.add_new_task) // item объект на который нажал пользователь
        {
            final EditText userTaskField = new EditText(this);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Добавление задания")
                    .setMessage("Что бы вы хотели добавить?")
                    .setView(userTaskField)
                    .setPositiveButton("Добавить", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            String task = String.valueOf(userTaskField.getText()); // получаем данные из всплывающего текстфилд
                            dataBase.insertData(task);
                            loadAllTask(); // задание останется после перезахода
                        }
                    })
                    .setNegativeButton("Ничего", null)
                    .create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteTask(View button) // кнопка удаления
    {
        View parent = (View) button.getParent(); // получаем слой родительский
        TextView textView = parent.findViewById(R.id.text_table_row); // что бы считать значение поля
        final String task = String.valueOf(textView.getText());

        parent.animate().alpha(0).setDuration(1500).withEndAction(new Runnable() // withEndAction отслеживает конец анимации
        {
            @Override
            public void run()
            {
                dataBase.deleteData(task);
                loadAllTask();
                parent.animate().alpha(1).setDuration(0);
            }
        });
    }

}
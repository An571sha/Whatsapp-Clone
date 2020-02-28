package com.example.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Test extends AppCompatActivity {
    ArrayList<Menu> menu_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initUi();
    }

    public void initUi(){
        RecyclerView menu_recycler = (RecyclerView) findViewById(R.id.main_menu_recycler_view);

        //set the layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation( linearLayoutManager.VERTICAL );
        menu_recycler.setLayoutManager( linearLayoutManager );

        //set adapter
        MainMenuAdapter mainMenuAdapter = new MainMenuAdapter( getMenulist() );
        menu_recycler.setAdapter(mainMenuAdapter);
    }

    public ArrayList<Menu> getMenulist() {

        menu_list = new ArrayList<Menu>();

        menu_list.add( new Menu("About Us", " Learn more about our values, mission and vision ") );
        menu_list.add( new Menu("Services ", " In implementing its mandate, the Agency will provide the following to its external and internal customers:\n"));
        menu_list.add( new Menu("Stakeholders", " Who are the key players, Find out more") );
        menu_list.add( new Menu("Learn about Counterfeits", "Gain more insight on counterfeit products"));
        return menu_list;
    }

    public class Menu {
        public String title, details;

        public Menu(String title, String details){
            this.details = details;
            this.title = title;
        }

    }

    public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.MyViewHolder> {

        public List<Menu> list_of_menus;

        public MainMenuAdapter( List<Menu> list_of_menus){
            this.list_of_menus = list_of_menus;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row,parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Menu menu = list_of_menus.get(position);
            Log.e("MENU IN ADAPTER TITLE", menu.title);
            holder.title_text.setText( menu.title );
            holder.details_text.setText( menu.details );

        }


        @Override
        public int getItemCount() {
            return list_of_menus.size();
        }

    /*
      View Holder class
    * */

        public class MyViewHolder extends RecyclerView.ViewHolder{
            public TextView title_text, details_text;

            public MyViewHolder(View itemView) {
                super(itemView);
                title_text = (TextView) itemView.findViewById(R.id.menu_title);
                details_text = (TextView) itemView.findViewById(R.id.menu_details);
            }
        }

    }



}

package com.andexert.expandablelayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.andexert.expandablelayout.library.ExpandableLayoutItem;
import com.andexert.expandablelayout.library.ExpandableLayoutListView;


public class MainActivity extends Activity {

    private final String[] array = {"Hello", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "Hello", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "Hello", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StringAdapter adapter = new StringAdapter(this, array);
        final ExpandableLayoutListView expandableLayoutListView = (ExpandableLayoutListView) findViewById(R.id.listview);

        expandableLayoutListView.addHeaderView(new Button(this), null, false);
        expandableLayoutListView.addFooterView(new Button(this), null, false);
        expandableLayoutListView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class StringAdapter extends ArrayAdapter<String> implements ExpandableLayoutItem.OnItemExpandListener {
        private LayoutInflater mInflater;

        public StringAdapter(Context context, String[] items) {
            super(context, R.layout.view_row, items);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.view_row, parent, false);
            ExpandableLayoutItem expItem = (ExpandableLayoutItem) convertView.findViewById(R.id.row);
            expItem.setOnItemExpandListener(this);
            TextView tv = (TextView) convertView.findViewById(R.id.header_text);
            tv.setText(getItem(position));
            return convertView;
        }

        @Override
        public void onExpand(ExpandableLayoutItem view) {
            view.setBackgroundColor(Color.GREEN);
        }

        @Override
        public void onCollapse(ExpandableLayoutItem view) {
            view.setBackgroundColor(Color.parseColor("#e74c3c"));
        }
    }
}

package com.max.caloriecruncher;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NewExerciseDialogFragment.NoticeDialogListener {


    public static Exercise adding;
    private MyAdapter mAdapter;
    List<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        final Spinner spinner = (Spinner) findViewById(R.id.exercise_selection);
        final TextView units = (TextView) findViewById(R.id.units);
        final EditText ex_amount = (EditText) findViewById(R.id.ex_amount);
        final EditText calories = (EditText) findViewById(R.id.calories);
        final ListView alternates = (ListView) findViewById(R.id.alternate_list);

        final List<AlternateCardInfo> alternatesData = new ArrayList<>();




        // Spinner Drop down elements
        categories = new ArrayList<String>();
        categories.add("Jogging");
        categories.add("Pushups");
        categories.add("Situps");
        categories.add("Squats");
        categories.add("Leg-Lifts");
        categories.add("Plank");
        categories.add("Jumping Jacks");
        categories.add("Pull-ups");
        categories.add("Cycling");
        categories.add("Walking");
        categories.add("Swimming");
        categories.add("Stair-Climbing");

        for(String type:categories) {
            Exercise e = Exercise.getExercise(type);
            alternatesData.add(new AlternateCardInfo(type, e.getType(), 0));
        }
        mAdapter = new MyAdapter(getApplicationContext(), alternatesData);
        AlternateCardInfo.setAdapter(mAdapter);

        mAdapter.notifyDataSetInvalidated();
        alternates.setAdapter(mAdapter);

        ex_amount.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String amt = v.getText().toString();
                    double c = Exercise.update_ex(amt, spinner, calories, units, alternatesData);
                    Exercise.update_alternates(c, AlternateCardInfo.getAll_cards());
                    return true;
                }
                return false;
            }
        });

        calories.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String cals = v.getText().toString();
                    double c = Exercise.update_cals(cals, spinner, ex_amount, units, alternatesData);
                    Exercise.update_alternates(c, AlternateCardInfo.getAll_cards());
                    return true;
                }
                return false;
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoticeDialog();
            }
        });

        // Spinner element


        // Spinner click listener
        spinner.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();

                Exercise.update_ex(ex_amount.getText().toString(), spinner, calories, units, alternatesData);
                String amt = ex_amount.getText().toString();
                double c = Exercise.update_ex(amt, spinner, calories, units, alternatesData);
                Exercise.update_alternates(c, AlternateCardInfo.getAll_cards());
            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    public class MyAdapter extends ArrayAdapter<AlternateCardInfo> {

        private ArrayList<AlternateCardInfo> mData = new ArrayList<>();
        private final Context context;
        private final List<AlternateCardInfo> values;


        public MyAdapter(Context context, List<AlternateCardInfo> values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }


        @Override
        public int getCount() {
            return values.size();
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        public void addItem(final AlternateCardInfo item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.alternate_exercise_card, parent, false);
            TextView typeText = (TextView) rowView.findViewById(R.id.type);
            typeText.setText(values.get(position).getName());
            TextView unitText = (TextView) rowView.findViewById(R.id.units);
            unitText.setText(values.get(position).getUnits());
            TextView amountText = (TextView) rowView.findViewById(R.id.equiv_amount);
            amountText.setText(Double.toString(values.get(position).getAmount()));

            return rowView;
        }
    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        adding = new Exercise();
        final DialogFragment dialog = new NewExerciseDialogFragment();

        dialog.show(getFragmentManager(), "NewExerciseDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick() {
        if(adding==null || adding.getExName()==null || adding.getRatio()==0) {
            Toast.makeText(MainActivity.this, "Not a valid exercise.", Toast.LENGTH_LONG).show();
        }
        new AlternateCardInfo(adding.getExName(), adding.getType(), 0);
        categories.add(adding.getExName());
        Exercise.addExercise(adding);
        Exercise.refresh_alternates();
    }

    @Override
    public void onDialogNegativeClick() {
        // User touched the dialog's negative button

    }

    public static class AlternateCardInfo {
        private String name;
        private String units;
        private double amount;
        private static MyAdapter adapter;
        private static List<AlternateCardInfo> all_cards = new ArrayList<>();

        public AlternateCardInfo(String name, String units, double amount) {
            this.name = name;
            this.units = units;
            this.amount = amount;
            all_cards.add(this);
        }

        public void updateCards() {
            adapter.clear();
            adapter.addAll(MainActivity.AlternateCardInfo.getAll_cards());
            adapter.notifyDataSetInvalidated();
        }

        public String getName() {
            return name;
        }

        public String getUnits() {
            return units;
        }

        public double getAmount() {
            return amount;
        }


        public void setAmount(double amt) {
            this.amount = amt;
            adapter.notifyDataSetChanged();
        }
        public static void setAdapter(MyAdapter b) {
            adapter = b;
        }
        public static MyAdapter getAdapter() {
            return adapter;
        }
        public static List<AlternateCardInfo> getAll_cards() {
            return all_cards;
        }
    }
}

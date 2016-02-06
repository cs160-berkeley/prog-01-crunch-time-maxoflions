package com.max.caloriecruncher;

import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Max on 2/1/2016.
 */
public class Exercise
{
    private String _name;
    private double _cal_ratio;
    private String _type;
    private static double last_cals;

    static final Map<String, Exercise> exercises = new HashMap<String, Exercise>();
    static {
        exercises.put("Pushups", new Exercise("Pushups", 350, "reps"));
        exercises.put("Situps", new Exercise("Situps", 200, "reps"));
        exercises.put("Squats", new Exercise("Squats", 225, "reps"));
        exercises.put("Leg-Lifts", new Exercise("Leg-Lifts", 25, "minutes"));
        exercises.put("Plank", new Exercise("Plank", 25, "minutes"));
        exercises.put("Jumping Jacks", new Exercise("Jumping Jacks", 10, "minutes"));
        exercises.put("Pull-ups", new Exercise("Pull-ups", 100, "reps"));
        exercises.put("Cycling", new Exercise("Cycling", 12, "minutes"));
        exercises.put("Walking", new Exercise("Walking", 20, "minutes"));
        exercises.put("Jogging", new Exercise("Jogging", 12, "minutes"));
        exercises.put("Swimming", new Exercise("Swimming", 13, "minutes"));
        exercises.put("Stair-Climbing", new Exercise("Stair-Climbing", 15, "minutes"));
    }

    static void addExercise(Exercise e) {
        exercises.put(e.getExName(), e);
    }

    static Exercise getExercise(String name) {
        return exercises.get(name);
    }

    Exercise(String name, double cal_ratio, String type) {
        _name = name;
        _cal_ratio = cal_ratio;
        _type = type;
    }
    Exercise() {

    }

    public void set_cal_ratio(double _cal_ratio) {
        if(_cal_ratio == 0.0) {
            System.out.println("SETTING TO NULL");
        }
        this._cal_ratio = _cal_ratio;
    }

    public void set_name(String _name) {
        if(_name==null) {
            System.out.println("SETTING TO NULL");
        }
        System.out.println("Setting name to " + _name);
        this._name = _name;
    }

    public void set_type(String _type) {

        if(_type==null) {
            System.out.println("SETTING TO NULL");
        }
        this._type = _type;
    }

    static void addExercise(String name, double ratio, String type) {
        exercises.put(name, new Exercise(name, ratio, type));
    }

    double getRatio() {
        return _cal_ratio;
    }

    String getExName() {
        return _name;
    }
    String getType() {
        return _type;
    }

    /** Updates when calorie field is updated. */
    static double update_cals(String cals, Spinner sp, TextView amt_txt, TextView units, List<MainActivity.AlternateCardInfo> alts) {
        try {
            return update_cals(Double.parseDouble(cals), sp, amt_txt, units, alts);
        } catch(NumberFormatException e) {
            return update_cals(0.0, sp, amt_txt, units, alts);
        }
    }

    /** Updates when exercise amount field is updated. */
    static double update_ex(String amt, Spinner sp, TextView cal_txt, TextView units, List<MainActivity.AlternateCardInfo> alts) {
        try {
            return update_ex(Double.parseDouble(amt), sp, cal_txt, units, alts);
        } catch(NumberFormatException e) {
            return update_cals(0.0, sp, cal_txt, units, alts);
        }
    }

    /** Updates when calorie field is updated. */
    static double update_cals(Double cals, Spinner sp, TextView amt_txt, TextView units, List<MainActivity.AlternateCardInfo> alts) {
        Exercise exer = exercises.get(sp.getSelectedItem().toString());
        double amt = cals * exer.getRatio() / 100;
        units.setText(exer.getType());
        amt_txt.setText(Double.toString(((double) Math.round(amt * 100)) / 100));
        last_cals = cals;
        return cals;
    }

    /** Updates when exercise amount is updated. */
    static double update_ex(Double amt, Spinner sp, TextView cals_txt, TextView units, List<MainActivity.AlternateCardInfo> alts) {
        Exercise exer = exercises.get(sp.getSelectedItem().toString());
        double cals = amt * 100 / exer.getRatio();
        units.setText(exer.getType());
        cals_txt.setText(Double.toString(((double) Math.round(cals * 100)) / 100));
        last_cals = cals;
        return cals;
    }

    static void refresh_alternates() {
        update_alternates(last_cals, MainActivity.AlternateCardInfo.getAll_cards());
    }

    static void update_alternates(Double cals, List<MainActivity.AlternateCardInfo> cards) {
        for (MainActivity.AlternateCardInfo card : cards) {
            Exercise exer = exercises.get(card.getName());
            double amt = cals * exer.getRatio() / 100;
            card.setAmount(((double)Math.round(amt * 100)) / 100);
        }
        MainActivity.MyAdapter adap = MainActivity.AlternateCardInfo.getAdapter();
        adap.clear();
        adap.addAll(MainActivity.AlternateCardInfo.getAll_cards());
        //        adap.notifyDataSetChanged();
        adap.notifyDataSetInvalidated();
    }
}

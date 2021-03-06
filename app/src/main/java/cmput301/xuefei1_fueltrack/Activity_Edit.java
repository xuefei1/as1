package cmput301.xuefei1_fueltrack;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by Fred on 2016/1/20.
 */
public class Activity_Edit extends Activity implements FTView {

    /*

    Purpose: Activity class designed for user to add a new log as well as updating an existing log

    Design rationale: View component in MVC, implements FTView interface and interacts with controller class ONLY

    Issues: None

    */

    private int activityType = Helper.ACTIVITY_TYPE_NEW_LOG;
    private TextView tv_main_title;
    private EditText et_amount;
    private EditText et_unit_price;
    private EditText et_type;
    private EditText et_station;
    private EditText et_odometer;
    private Button btn_date_picker;
    private Calendar cal;
    private Context context = this;
    private FTController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle extras = getIntent().getBundleExtra(Helper.ACTIVITY_BUNDLE_TITLE);
        if (extras != null) {
            this.activityType = extras.getInt(Helper.ACTIVITY_BUNDLE_ACTIVITY_TYPE);
        }
        this.controller = ((FTApplication)getApplication()).getAppController();
        cal = Calendar.getInstance();
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.edit_log_layout);
        LayoutInflater inflater = (LayoutInflater) getActionBar()
                .getThemedContext().getSystemService(
                        LAYOUT_INFLATER_SERVICE);
        View customActionBarView = inflater.inflate(
                R.layout.actionbar_edit_activity_custom, null);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        Button btn_save = (Button) findViewById(R.id.btn_save);
        Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkDataReady()){
                    if(activityType == Helper.ACTIVITY_TYPE_NEW_LOG) {
                        controller.addNewLog(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                                Float.valueOf(et_amount.getText().toString()),  Float.valueOf(et_unit_price.getText().toString()),
                                        Float.valueOf(et_odometer.getText().toString()),  et_type.getText().toString(),
                                        et_station.getText().toString());
                    }else{
                        // float new_amount, float new_price, float new_odometer, String new_station, String new_type, int new_year, int new_month, int new_day
                        controller.updateLog(extras.getInt(Helper.ACTIVITY_BUNDLE_INDEX),
                                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                                Float.valueOf(et_amount.getText().toString()),  Float.valueOf(et_unit_price.getText().toString()),
                                Float.valueOf(et_odometer.getText().toString()),  et_type.getText().toString(),
                                et_station.getText().toString()
                                );
                    }
                    finish();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityType == Helper.ACTIVITY_TYPE_EDIT_LOG) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle(context.getResources().getString(R.string.abort_title_en));
                    dialog.setMessage(context.getResources().getString(R.string.abort_msg_en));
                    dialog.setPositiveButton(context.getResources().getString(R.string.yes_en), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    dialog.setNegativeButton(context.getResources().getString(R.string.no_en), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    finish();
                }
            }
        });
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        et_amount = (EditText) findViewById(R.id.et_amount);
        et_unit_price = (EditText) findViewById(R.id.et_unit_price);
        et_type = (EditText) findViewById(R.id.et_type);
        et_station = (EditText) findViewById(R.id.et_station);
        et_odometer = (EditText) findViewById(R.id.et_odometer);
        btn_date_picker = (Button) findViewById(R.id.btn_date_picker);

        if(this.activityType == Helper.ACTIVITY_TYPE_EDIT_LOG){
            this.tv_main_title.setText(getResources().getString(R.string.edit_log_title_en));
        }else{
            this.tv_main_title.setText(getResources().getString(R.string.add_new_log_title_en));
        }

        final FTView ftView = this;

        final DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                controller.requestViewUpdate(ftView);
            }
        };

        btn_date_picker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DatePickerDialog p = new DatePickerDialog(context, onDateSetListener,
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                p.show();
            }
        });

        if(this.activityType == Helper.ACTIVITY_TYPE_EDIT_LOG){
            this.initializeValues(extras);
        }

        controller.requestViewUpdate(ftView);
    }

    @Override
    public void updateView(){
        btn_date_picker.setText(DateFormat.getDateInstance().format(cal.getTime()));
    }

    private void initializeValues(Bundle d){
        this.et_amount.setText(d.getString(Helper.ACTIVITY_BUNDLE_AMOUNT, "0.0"));
        this.et_unit_price.setText(d.getString(Helper.ACTIVITY_BUNDLE_UNIT_PRICE, "0.0"));
        this.et_station.setText(d.getString(Helper.ACTIVITY_BUNDLE_STATION, "Shell"));
        this.et_type.setText(d.getString(Helper.ACTIVITY_BUNDLE_GRADE, "Regular"));
        this.et_odometer.setText(d.getString(Helper.ACTIVITY_BUNDLE_ODOMETER, "0"));
        this.cal.set(Calendar.YEAR, d.getInt(Helper.ACTIVITY_BUNDLE_DATE_YEAR, 2015));
        this.cal.set(Calendar.MONTH, d.getInt(Helper.ACTIVITY_BUNDLE_DATE_MONTH, 01));
        this.cal.set(Calendar.DAY_OF_MONTH, d.getInt(Helper.ACTIVITY_BUNDLE_DATE_DAY, 01));
    }

    private void showNotificationDialog(String title, String msg){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(context.getResources().getString(R.string.ok_en), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private boolean checkDataReady(){
        String target = "";
        if(this.et_amount.getText().toString().matches("")){
            target = context.getResources().getString(R.string.amount_title_en);
        }
        else if(this.et_unit_price.getText().toString().matches("")){
            target = context.getResources().getString(R.string.unit_price_title_en);

        }
        else if(this.et_type.getText().toString().matches("")){
            target = context.getResources().getString(R.string.type_title_en);
        }
        else if(this.et_odometer.getText().toString().matches("")){
            target = context.getResources().getString(R.string.odometer_title_en);
        }
        else if(this.et_station.getText().toString().matches("")){
            target = context.getResources().getString(R.string.station_title_en);
        }
        if(!target.matches("")) {
            this.showNotificationDialog(context.getResources().getString(R.string.error_title_en), context.getResources().getString(R.string.incomplete_msg_en) + " \'"+target.substring(0, target.length()-1)+"\'");
            return false;
        }

        return true;
    }

}

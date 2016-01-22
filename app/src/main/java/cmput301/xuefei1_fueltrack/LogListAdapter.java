package cmput301.xuefei1_fueltrack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Fred on 2016/1/19.
 */
public class LogListAdapter extends ArrayAdapter {

    private Context ctx;
    private List<FuelLog> data;
    private int layoutResID;

    public LogListAdapter(Context context, int layoutResourceId, List<FuelLog> data){
        super(context, layoutResourceId, data);
        this.data = data;
        this.ctx = context;
        this.layoutResID = layoutResourceId;

    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public FuelLog getItem(int position) {
        return this.data.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
            convertView = inflater.inflate(layoutResID, parent, false);
        }

        Button bt_edit = (Button) convertView.findViewById(R.id.btn_edit_log);
        Button bt_delete = (Button) convertView.findViewById(R.id.btn_del_log);
        TextView tv_total_cost = (TextView) convertView.findViewById(R.id.tv_total_cost);
        TextView tv_odometer = (TextView) convertView.findViewById(R.id.tv_odometer);
        TextView tv_unit_cost = (TextView) convertView.findViewById(R.id.tv_unit_cost);
        TextView tv_amount = (TextView) convertView.findViewById(R.id.tv_amount);
        TextView tv_type = (TextView) convertView.findViewById(R.id.tv_type);
        TextView tv_date = (TextView) convertView.findViewById(R.id.tv_date);
        TextView tv_location = (TextView) convertView.findViewById(R.id.tv_location);

        final String cost =  FuelTrack_Utils.roundDecimal(2, this.data.get(position).getTotalCost());
        final String odometer = FuelTrack_Utils.roundDecimal(1, this.data.get(position).getOdometer());
        final String amount = FuelTrack_Utils.roundDecimal(3, this.data.get(position).getAmount());
        final String price = FuelTrack_Utils.roundDecimal(1, this.data.get(position).getUnitCost());

        tv_total_cost.setText("$" + cost);
        tv_odometer.setText("Odometer:  " + odometer +" km");
        tv_amount.setText("Amount:  " + amount + " L");
        tv_unit_cost.setText(price + " cents/L");
        tv_date.setText(this.data.get(position).getDate());
        tv_type.setText(this.data.get(position).getGrade());
        tv_location.setText("At " + this.data.get(position).getStation());

        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, FuelTrack_EditActivity.class);
                Bundle b = new Bundle();
                int year = data.get(position).getDateCalendar().get(Calendar.YEAR);
                int month = data.get(position).getDateCalendar().get(Calendar.MONTH);
                int day = data.get(position).getDateCalendar().get(Calendar.DAY_OF_MONTH);
                b.putInt(FuelTrack_Utils.ACTIVITY_BUNDLE_ACTIVITY_TYPE, FuelTrack_Utils.ACTIVITY_TYPE_EDIT_LOG);
                b.putString(FuelTrack_Utils.ACTIVITY_BUNDLE_GRADE, data.get(position).getGrade());
                b.putString(FuelTrack_Utils.ACTIVITY_BUNDLE_AMOUNT, amount);
                b.putString(FuelTrack_Utils.ACTIVITY_BUNDLE_STATION, data.get(position).getStation());
                b.putString(FuelTrack_Utils.ACTIVITY_BUNDLE_ODOMETER, odometer);
                b.putString(FuelTrack_Utils.ACTIVITY_BUNDLE_UNIT_PRICE, price);
                b.putInt(FuelTrack_Utils.ACTIVITY_BUNDLE_DATE_YEAR, year);
                b.putInt(FuelTrack_Utils.ACTIVITY_BUNDLE_DATE_MONTH, month);
                b.putInt(FuelTrack_Utils.ACTIVITY_BUNDLE_DATE_DAY, day);
                b.putInt(FuelTrack_Utils.ACTIVITY_BUNDLE_INDEX, position);
                intent.putExtra(FuelTrack_Utils.ACTIVITY_BUNDLE_TITLE, b);
                ctx.startActivity(intent);
            }
        });

        bt_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                dialog.setTitle(ctx.getResources().getString(R.string.delete_title_en));
                dialog.setMessage(ctx.getResources().getString(R.string.delete_msg_en));
                dialog.setPositiveButton(ctx.getResources().getString(R.string.yes_en), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data.remove(position);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton(ctx.getResources().getString(R.string.no_en), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        return convertView;
    }
}

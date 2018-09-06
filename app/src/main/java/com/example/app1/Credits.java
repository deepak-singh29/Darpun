package com.example.app1;

import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.view.Window;
import android.widget.TextView;

public class Credits extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_credits);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvDeepak,tvAvinash,tvRaja,tvPrath,tvUjjwal,tvNeeraj,tvNikhil,tvHeader;
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        String tvHeader_str = "<b>DARPUN Team</b> \n \n";
        tvDeepak = (TextView) findViewById(R.id.tvDeepak);
        String tvDeepak_str = "<b> D </b> - Deepak Kumar Singh";
        tvAvinash = (TextView) findViewById(R.id.tvAvinash);
        String tvAvinash_str = "<b> A </b> - Avinash Singh";
        tvRaja = (TextView) findViewById(R.id.tvRaja);
        String tvRaja_str = "<b> R </b> - Rajagopal Rao";
        tvPrath = (TextView) findViewById(R.id.tvPrathmesh);
        String tvPrath_str = "<b> P </b> - Prathmesh Dali";
        tvUjjwal = (TextView) findViewById(R.id.tvUjjwal);
        String tvUjjwal_str = "<b> U </b> - Ujjwal Saxena";
        tvNeeraj = (TextView) findViewById(R.id.tvNeeraj);
        String tvNeeraj_str = "<b> N </b> - Neeraj Gulia";
        tvNikhil = (TextView) findViewById(R.id.tvNikhil);
        String tvNikhil_str = "<b>   </b> - Nikhil Sinnarkar";

        tvHeader.setText(Html.fromHtml(tvHeader_str));
        tvDeepak.setText(Html.fromHtml(tvDeepak_str));
        tvAvinash.setText(Html.fromHtml(tvAvinash_str));
        tvRaja.setText(Html.fromHtml(tvRaja_str));
        tvPrath.setText(Html.fromHtml(tvPrath_str));
        tvUjjwal.setText(Html.fromHtml(tvUjjwal_str));
        tvNeeraj.setText(Html.fromHtml(tvNeeraj_str));
        tvNikhil.setText(Html.fromHtml(tvNikhil_str));

    }

}

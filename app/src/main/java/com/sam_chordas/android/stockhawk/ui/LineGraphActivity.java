package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.retrofit.ApiService;
import com.sam_chordas.android.stockhawk.retrofit.RestClient;
import com.sam_chordas.android.stockhawk.retrofit.model.Quote;
import com.sam_chordas.android.stockhawk.retrofit.model.QuoteData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LineGraphActivity extends AppCompatActivity {
    LineChartView lineView;
    private final String LOG_TAG = LineGraphActivity.class.getSimpleName();
    private Context context = null;
    ArrayList<Quote> quoteArray = null;
    public String symbol;
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        symbol = getIntent().getExtras().getString("symbol");

        setTitle(symbol);

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String endDate = dateFormat.format(System.currentTimeMillis());

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -30);
        String startDate = dateFormat.format(cal.getTimeInMillis());

        fetchQuoteHistoryData(symbol, startDate, endDate);

        context = this;
        lineView = (LineChartView) findViewById(R.id.linechart);
    }

    private void fetchQuoteHistoryData(String symbol, String startDate, String endDate){

        ApiService apiService = RestClient.getApiService();

        String q = "select * from yahoo.finance.historicaldata where symbol = \""+symbol+"\" and startDate = \""+startDate+"\" and endDate = \""+endDate+"\"";
        String diagnostics = "true";
        String env = "store://datatables.org/alltableswithkeys";
        String format = "json";

        Call<QuoteData> call = apiService.getHistoryData(q, diagnostics, env, format);

        call.enqueue(new Callback<QuoteData>() {
            @Override
            public void onResponse(Response<QuoteData> response, Retrofit retrofit) {
                QuoteData data = response.body();

                ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);

                if(data != null){

                    if(data.query != null) {
                        Log.d(LOG_TAG, "Query is not null: ");

                        if (data.query.results != null) {
                            Log.d(LOG_TAG, "Results is not null: ");

                            if (data.query.results.quote != null) {
                                Log.d(LOG_TAG, "Quote is not null: ");

                                quoteArray = data.query.results.quote;
                                // returned array is in latest to old order.
                                Collections.reverse(quoteArray);
                                Log.d(LOG_TAG, "Quote data: symbol: "+quoteArray.get(0).symbol+" date: "+ quoteArray.get(0).quote_date +" bidprice: "+quoteArray.get(0).close);

                                if (quoteArray.size() > 0) {
                                    lineView.setVisibility(View.VISIBLE);
                                    displayGraph();
                                } else {
                                    Toast.makeText(context, "No Data Received", Toast.LENGTH_SHORT).show();
                                    Log.e(LOG_TAG, "NO DATA: ");
                                }
                            }
                        }
                    }

                } else{
                    Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "ERROR");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(context, "Call to server failed", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Call to server failed: " + t.getMessage());
            }
        });
    }

    public void displayGraph() {
        /* LineSet dataset = new LineSet();
        dataset.addPoint(new Point("1", 5.5f));
        dataset.addPoint(new Point("2", 15.5f));
        dataset.addPoint(new Point("3", 25.5f));
        dataset.addPoint(new Point("4", 35.5f));
        dataset.addPoint(new Point("5", 25.5f));
        dataset.addPoint(new Point("6", 5.5f));
        dataset.addPoint(new Point("7", 15.5f));

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);


        lineView = (LineChartView) findViewById(R.id.linechart);

        dataset.setDotsColor(getResources().getColor(R.color.material_red_700));
        dataset.setColor(getResources().getColor(R.color.material_green_700));

        lineView.setAxisBorderValues((int) 5.5 - 2, (int) 35.5 + 2);
        lineView.setAxisColor(Color.WHITE);
        lineView.setLabelsColor(Color.WHITE);
        lineView.setStep(1);
        lineView.setGrid(ChartView.GridType.FULL, paint);
        lineView.addData(dataset);
        lineView.show();
    */

        LineSet dataset = new LineSet();
        double minValue = 0;
        double maxValue = 0;
        int calls = 1;

        if (quoteArray.size() > 0) {

            double bidprice;
            bidprice = Double.parseDouble(quoteArray.get(0).close);
            minValue = bidprice;
            maxValue = bidprice;

            for (Quote quote : quoteArray) {

                bidprice = Double.parseDouble(quote.close);
                dataset.addPoint(new Point(String.valueOf(calls), Float.parseFloat(quote.close)));

                if (minValue > bidprice) {
                    minValue = bidprice;
                }
                if (maxValue < bidprice) {
                    maxValue = bidprice;
                }
                calls++;
            }

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);

            dataset.setDotsColor(getResources().getColor(R.color.material_red_700));
            dataset.setColor(getResources().getColor(R.color.material_green_700));

            lineView.dismiss();
            lineView.addData(dataset);
            lineView.setAxisBorderValues((int) minValue - 2, (int) maxValue + 2);
            lineView.setAxisColor(Color.WHITE);
            lineView.setLabelsColor(Color.WHITE);
            lineView.setStep(1);
            lineView.setGrid(ChartView.GridType.FULL, paint);
            lineView.show();
        }
    }
}

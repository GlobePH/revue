package com.cryptonice.revue;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductInformation extends AppCompatActivity {

    TextView txt_act_prod_name, txt_act_prod_mfg, txt_rating_1, txt_rating_2, txt_rating_3, txt_prod_about;
    ImageView  image_act_1, image_act_2, image_act_3;
    RatingBar rating_1, rating_2, rating_3;
    Button btn_rate, btn_review, btn_visit;
    ListView list_comments;
    public String product_id = MainActivity.selected_item;
    Connection connection = MainActivity.connection;
    ArrayList<String> user_ids = new ArrayList<String>();
    public static ProductInformation.ReviewListAdapter adapter;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void populateReviews() {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT user_id FROM product_review WHERE product_id = '" + MainActivity.selected_item + "'");
            while(rs.next()) {
                user_ids.add(rs.getString(1));
            }
            adapter = new ProductInformation.ReviewListAdapter(getApplicationContext(), R.layout.comment_layout, user_ids);
            list_comments.setAdapter(adapter);
        } catch (Exception ex) {

        }
    }

    public void retrieveInformation() {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM product_info WHERE product_id = '" + product_id + "'");
            rs.first();
            txt_act_prod_name.setText(rs.getString(2));
            txt_act_prod_mfg.setText(rs.getString(3));
            txt_prod_about.setText(rs.getString(4));
            Statement statement2 = connection.createStatement();
            ResultSet rs2 = statement2.executeQuery("SELECT product_id, AVG(rating) FROM" +
                    " product_rating WHERE product_id = '" + product_id + "' and rating_type = 'build_design'");
            rs2.first();

            rating_1.setRating(rs2.getFloat(2));
            txt_rating_1.setText(String.format("%.1f", rs2.getFloat(2)));

            rs2 = statement2.executeQuery("SELECT product_id, AVG(rating) FROM" +
                    " product_rating WHERE product_id = '" + product_id + "' and rating_type = 'durability'");
            rs2.first();

            rating_2.setRating(rs2.getFloat(2));
            txt_rating_2.setText(String.format("%.1f", rs2.getFloat(2)));

            rs2 = statement2.executeQuery("SELECT product_id, AVG(rating) FROM" +
                    " product_rating WHERE product_id = '" + product_id + "' and rating_type = 'performance'");
            rs2.first();

            rating_3.setRating(rs2.getFloat(2));
            txt_rating_3.setText(String.format("%.1f", rs2.getFloat(2)));

            rs2 = statement2.executeQuery("SELECT * FROM" +
                    " product_photos WHERE product_id = '" + product_id + "' and image_type != 'primary'");
            rs2.first();
            byte[] blob = rs2.getBlob(2).getBytes(1, (int)rs2.getBlob(2).length());
            image_act_1.setImageBitmap(BitmapFactory.decodeByteArray(blob,
                    0,blob.length));
            rs2.next();
            blob = rs2.getBlob(2).getBytes(1, (int)rs2.getBlob(2).length());
            image_act_2.setImageBitmap(BitmapFactory.decodeByteArray(blob,
                    0,blob.length));
            rs2.next();
            blob = rs2.getBlob(2).getBytes(1, (int)rs2.getBlob(2).length());
            image_act_3.setImageBitmap(BitmapFactory.decodeByteArray(blob,
                    0,blob.length));

        } catch (Exception ex) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_information);
        txt_act_prod_name = (TextView) findViewById(R.id.txt_act_prod_name);
        txt_act_prod_mfg = (TextView) findViewById(R.id.txt_act_prod_mfg);
        txt_prod_about = (TextView) findViewById(R.id.txt_prod_about);
        txt_rating_1 = (TextView) findViewById(R.id.txt_rating_1);
        txt_rating_2 = (TextView) findViewById(R.id.txt_rating_2);
        txt_rating_3 = (TextView) findViewById(R.id.txt_rating_3);
        image_act_1 = (ImageView) findViewById(R.id.image_act_1);
        image_act_2 = (ImageView) findViewById(R.id.image_act_2);
        image_act_3 = (ImageView) findViewById(R.id.image_act_3);
        rating_1 = (RatingBar) findViewById(R.id.rating_1);
        rating_2 = (RatingBar) findViewById(R.id.rating_2);
        rating_3 = (RatingBar) findViewById(R.id.rating_3);
        btn_rate = (Button) findViewById(R.id.btn_rate);
        btn_review = (Button) findViewById(R.id.btn_review);
        btn_visit = (Button) findViewById(R.id.btn_visit);
        list_comments = (ListView) findViewById(R.id.list_comments);

        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                ReviewDialog review_dialog = new ReviewDialog();
                review_dialog.show(fm, "Give Review");
            }
        });
        retrieveInformation();
        populateReviews();
    }

    public class ReviewListAdapter extends ArrayAdapter<String> {
        private int layout;
        private ReviewListAdapter (Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public void notifyDataSetChanged() {
            populateReviews();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ProductInformation.ListViewViews mainHolder = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ProductInformation.ListViewViews holder = new ProductInformation.ListViewViews();
                holder.txt_review_content = (TextView) convertView.findViewById(R.id.txt_review_content);
                holder.txt_review_name = (TextView) convertView.findViewById(R.id.txt_review_name);

                convertView.setTag(holder);
            }
            else {

            }

            mainHolder = (ProductInformation.ListViewViews) convertView.getTag();
            String user_id = getItem(position);

            try {
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT review FROM" +
                        " product_review WHERE user_id = '" + user_id + "' and product_id = '" + MainActivity.selected_item + "'");
                rs.first();
                mainHolder.txt_review_content.setText(rs.getString(1));
                rs = statement.executeQuery("SELECT first_name, last_name FROM" +
                        " user_info WHERE user_id = '" + user_id + "'");
                rs.first();
                mainHolder.txt_review_name.setText(rs.getString(1) + " " + rs.getString(2));
            } catch (Exception ex) {

            }
            btn_rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = getSupportFragmentManager();
                    RateDialog rate_dialog = new RateDialog();
                    rate_dialog.show(fm, "Rate Product");
                }
            });
            btn_visit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("http://www.asianic.com.ph");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    public class ListViewViews {
        TextView txt_review_name, txt_review_content;
    }
}

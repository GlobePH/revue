package com.cryptonice.revue;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.facebook.Profile;

import java.sql.ResultSet;
import java.sql.Statement;

public class RateDialog extends DialogFragment {

    View view;
    Button btn_rate;
    RatingBar rate_1, rate_2, rate_3;
    EditText txt_dialog_review;

    public void RateDialog() {

    }

    public void validateRate() {
        if (rate_1.getRating() > 0 && rate_2.getRating() > 0 && rate_3.getRating() > 0) {
            try {
                Statement statement = MainActivity.connection.createStatement();
                statement.execute("INSERT INTO product_rating(product_id, user_id, rating,type, rating) " +
                        "VALUES('" + MainActivity.selected_item +
                        "', '" + Profile.getCurrentProfile().getId() + "', 'build_design', '" + (int)rate_1.getRating() +
                        ");");
                statement = MainActivity.connection.createStatement();
                statement.execute("INSERT INTO product_rating(product_id, user_id, rating,type, rating) " +
                        "VALUES('" + MainActivity.selected_item +
                        "', '" + Profile.getCurrentProfile().getId() + "', 'durability', '" + (int)rate_2.getRating() +
                        ");");
                statement = MainActivity.connection.createStatement();
                statement.execute("INSERT INTO product_rating(product_id, user_id, rating,type, rating) " +
                        "VALUES('" + MainActivity.selected_item +
                        "', '" + Profile.getCurrentProfile().getId() + "', 'performance', '" + (int)rate_3.getRating() +
                        ");");
            } catch (Exception ex) {
                System.out.printf(ex.getMessage());
            }
            ProductInformation.adapter.notifyDataSetChanged();
            Toast.makeText(this.getActivity(), "Thank your for rating this product!", Toast.LENGTH_SHORT).show();
            dismiss();
        } else {
            Toast.makeText(this.getActivity(), "Please accomplish all rating bars.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.rate_dialog, container, false);
        rate_1 = (RatingBar) view.findViewById(R.id.rate_1);
        rate_2 = (RatingBar) view.findViewById(R.id.rate_2);
        rate_3 = (RatingBar) view.findViewById(R.id.rate_3);
        btn_rate = (Button) view.findViewById(R.id.btn_rate);
        btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateRate();
            }
        });
        return view;
    }

}

package com.cryptonice.revue;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.Profile;

import java.sql.ResultSet;
import java.sql.Statement;

public class ReviewDialog extends DialogFragment {

    View view;
    Button btn_submit_review;
    EditText txt_dialog_review;

    public void ReviewDialog() {

    }

    public void validateReview() {
        if (txt_dialog_review.getText().length() == 0) {
            Toast.makeText(this.getActivity(), "Invalid review.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                Statement statement = MainActivity.connection.createStatement();
                statement.execute("INSERT INTO product_review(product_id, user_id, review) VALUES('" + MainActivity.selected_item +
                        "', '" + Profile.getCurrentProfile().getId() + "', '" + txt_dialog_review.getText() + "');");
            } catch (Exception ex) {
                System.out.printf(ex.getMessage());
            }
            ProductInformation.adapter.notifyDataSetChanged();
            Toast.makeText(this.getActivity(), "Thank your for giving a review!", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.review_dialog, container, false);
        txt_dialog_review = (EditText) view.findViewById(R.id.txt_dialog_review);
        btn_submit_review = (Button) view.findViewById(R.id.btn_submit_review);
        btn_submit_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateReview();
            }
        });
        return view;
    }

}

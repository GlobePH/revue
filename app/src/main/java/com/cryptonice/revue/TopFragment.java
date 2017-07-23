package com.cryptonice.revue;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TopFragment extends Fragment {

    public static View content_view;
    public RecyclerView reclr_top_mobile;
    public Connection connection;
    public String SELECT_ALL_RANKED_DEVICES = "SELECT product_id, AVG(rating) FROM `product_rating` GROUP BY product_id ORDER BY AVG(rating) DESC";
    public String SELECT_ALL_MOBILE_PHONES = "SELECT * FROM product_info WHERE category = 'mobile_phone'";
    ArrayList<String> mobile_ID = new ArrayList<String>();



    public TopFragment() {
        // Required empty public constructor
    }

    public void populateItems() {
        List<ProductInfo> mobile_list = new ArrayList<ProductInfo>();
        try {
            Statement statement = connection.createStatement();
            ResultSet ranked_devices = statement.executeQuery(SELECT_ALL_RANKED_DEVICES);

            int mobile_count = 0, laptop_count = 0, accessory_count = 0, tablet_count = 0;

            while (ranked_devices.next()) {
                float rate = ranked_devices.getFloat(2);
                if (mobile_count < 5) {
                    System.out.println(ranked_devices.getString(1));
                    String query = "SELECT * FROM product_info WHERE category = 'mobile_phone' and product_id = '" +
                            ranked_devices.getString(1) + "'";
                    Statement statement2 = connection.createStatement();
                    ResultSet mobile_device = statement2.executeQuery(query);
                    while (mobile_device.next()) {
                        mobile_count += 1;
                        ProductInfo product_info = new ProductInfo();
                        mobile_ID.add(mobile_device.getString(1));
                        product_info.product_name = mobile_device.getString(2);
                        product_info.product_manufacturer = mobile_device.getString(3);
                        product_info.product_rating = rate;
                        Statement statement3 = connection.createStatement();
                        ResultSet result_set2 = statement3.executeQuery("SELECT * FROM product_photos WHERE product_id ='"
                                + mobile_device.getString(1) + "' and image_type = 'primary'");
                        product_info.product_image = null;
                        while (result_set2.next())
                            product_info.product_image = result_set2.getBlob(2).getBytes(1, (int)result_set2.getBlob(2).length());
                        mobile_list.add(product_info);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());

        }

        CardAdapter card_adapter = new CardAdapter(mobile_list);
        reclr_top_mobile.setAdapter(card_adapter);

    }

    public void setRecyclerView() {
        reclr_top_mobile = (RecyclerView) content_view.findViewById(R.id.reclr_top_mobile);
        reclr_top_mobile.setHasFixedSize(true);
        LinearLayoutManager linear_layout_manager = new LinearLayoutManager(TopFragment.this.getContext());
        linear_layout_manager.setOrientation(LinearLayoutManager.VERTICAL);
        reclr_top_mobile.setLayoutManager(linear_layout_manager);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        content_view = inflater.inflate(R.layout.fragment_top, container, false);
        setRecyclerView();
        populateItems();
        return content_view;
    }


    public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

        private List<ProductInfo> product_list;

        public CardAdapter(List<ProductInfo> product_list) {
            this.product_list = product_list;
        }

        @Override
        public int getItemCount() {
            return product_list.size();
        }

        @Override
        public void onBindViewHolder(CardViewHolder card_view_holder, int i) {
            ProductInfo product_info = product_list.get(i);
            card_view_holder.product_name.setText(product_info.product_name);
            String rate = String.format("%.1f", product_info.product_rating);
            card_view_holder.product_rating.setText(rate + " ★");
            card_view_holder.manufacturer.setText(product_info.product_manufacturer);
            card_view_holder.product_image.setImageBitmap(BitmapFactory.decodeByteArray(product_info.product_image,
                    0,product_info.product_image.length));
            card_view_holder.product_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            System.out.println("done");
        }

        private final View.OnClickListener on_recycler_item_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = reclr_top_mobile.getChildLayoutPosition(v);
                MainActivity.selected_item = mobile_ID.get(itemPosition);
                Intent intent = new Intent(TopFragment.this.getActivity(), ProductInformation.class);
                startActivityForResult(intent, 0);
            }
        };

        @Override
        public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card_product_info, viewGroup, false);
            itemView.setOnClickListener(on_recycler_item_click);
            return new CardViewHolder(itemView);
        }

        public class CardViewHolder extends RecyclerView.ViewHolder {
            public TextView product_name, manufacturer, product_rating;
            public ImageView product_image;

            public CardViewHolder(View view) {
                super(view);
                product_name = (TextView) view.findViewById(R.id.txt_card_product_name);
                manufacturer = (TextView) view.findViewById(R.id.txt_card_manufacturer);
                product_rating = (TextView) view.findViewById(R.id.txt_card_rating);
                product_image = (ImageView) view.findViewById(R.id.img_card_product);
            }
        }
    }

    public class ProductInfo {
        public String product_name;
        public String product_manufacturer;
        public byte[] product_image;
        public float product_rating;
    }

}

package com.example.pocopenstreetmap.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.android.volley.VolleyError;
import com.example.pocopenstreetmap.R;
import com.example.pocopenstreetmap.controller.ApiController;
import com.example.pocopenstreetmap.model.Post;
import com.example.pocopenstreetmap.model.ResponsePosts;

public class NearFragment extends Fragment {

    public NearFragment() {
        // Required empty public constructor
    }

    public static NearFragment newInstance() {
        return new NearFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_near, container, false);

        try {
            final TextView textView = view.findViewById(R.id.text_near);
            ApiController apiController = new ApiController(getContext());

            apiController.fetchPosts(new ApiController.ApiCallback() {
                @Override
                public void onSuccess(ResponsePosts responsePosts) {
                    StringBuilder sb = new StringBuilder();

                    if (responsePosts != null && responsePosts.getPosts() != null) {
                        for (Post post : responsePosts.getPosts()) {
                            sb.append("ID: ").append(post.getId()).append("\n");
                            sb.append("Message: ").append(post.getMessage()).append("\n");
                            sb.append("GPS Location: ").append(post.getGPS_location()).append("\n");/*
                            if (post.getAttachment() != null && !post.getAttachment().isEmpty()) {
                                sb.append("Attachment URL: ").append(post.getAttachment().get(0).getUrl()).append("\n");
                            }
                            sb.append("Creator Name: ").append(post.getCreator().getName()).append("\n");
                            sb.append("Creator Email: ").append(post.getCreator().getEmail()).append("\n\n");
                            */
                        }
                    } else {
                        Log.e("NearFragment", "ResponsePosts or Posts are null");
                        sb.append("No data available");
                    }
                    textView.setText(sb.toString());
                }

                @Override
                public void onError(VolleyError error) {
                    textView.setText("Error fetching data: " + error.getMessage());
                }

            });
        } catch (Exception e) {
            final TextView textView = view.findViewById(R.id.textView);
            Log.e("Error", e.getMessage());
        }

        return view;
    }
}
package com.example.photoviewer;

import android.content.Intent;
import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.net.URL;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PostAdapter
        extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final JSONArray posts;

    // Constructor
    public PostAdapter(JSONArray posts) {
        this.posts = posts;
    }

    // Create a layout for each post
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);

        return new PostViewHolder(view);
    }

    // Display post information
    @Override
    public void onBindViewHolder(
            @NonNull PostViewHolder holder, int position) {

        try {
            JSONObject post = posts.getJSONObject(position);

            String title = post.optString("title", "");
            String description = post.optString("text", "");

            holder.postTitle.setText(title);
            holder.postDescription.setText(description);
            holder.itemView.setOnLongClickListener(v -> {
                String imageLink = post.optString("image", "");

                String shareText = title + "\n\n"
                        + description + "\n\n"
                        + imageLink;

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareText);

                Intent chooser = Intent.createChooser(
                        intent,
                        "Share Post"
                );

                v.getContext().startActivity(chooser);
                return true;
            });


            String imageUrl = post.optString("image", "");

            holder.postImage.setImageDrawable(null);
            holder.postImage.setTag(imageUrl);

            holder.postImage.setOnClickListener(v -> {
                if (imageUrl.isEmpty() || imageUrl.equals("null")) {
                    return;
                }

                Dialog dialog = new Dialog(v.getContext());

                ImageView fullImage = new ImageView(v.getContext());
                fullImage.setAdjustViewBounds(true);
                fullImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                fullImage.setBackgroundColor(android.graphics.Color.BLACK);

                dialog.setContentView(fullImage);

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );
                }

                fullImage.setImageDrawable(holder.postImage.getDrawable());
                fullImage.setOnClickListener(view -> dialog.dismiss());

                dialog.show();

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );
                }
            });

            if (!imageUrl.isEmpty() && !imageUrl.equals("null")) {

                new Thread(() -> {
                    try {
                        URL url = new URL(imageUrl);

                        Bitmap bitmap = BitmapFactory.decodeStream(
                                url.openStream()
                        );

                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (imageUrl.equals(holder.postImage.getTag())) {
                                holder.postImage.setImageBitmap(bitmap);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }


        } catch (JSONException e) {
            holder.postTitle.setText("Error loading post");
            holder.postDescription.setText("");
        }
    }

    // Total number of posts
    @Override
    public int getItemCount() {
        return posts.length();
    }

    // Connect Java with item_post.xml
    public static class PostViewHolder
            extends RecyclerView.ViewHolder {

        ImageView postImage;
        TextView postTitle;
        TextView postDescription;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.postImage);

            postTitle = itemView.findViewById(R.id.postTitle);

            postDescription =
                    itemView.findViewById(R.id.postDescription);
        }
    }
}

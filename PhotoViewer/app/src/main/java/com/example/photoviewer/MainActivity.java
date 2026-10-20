package com.example.photoviewer;
import android.content.ContentResolver;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import java.util.ArrayList;
import java.util.Collections;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.LinearLayout;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import android.net.Uri;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.widget.TextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;
import org.json.JSONArray;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button btnLoad;
    RecyclerView recyclerView;
    EditText searchEditText;
    JSONArray allPosts = new JSONArray();
    Button btnSort;
    Button btnViewMode;
    boolean isGridView = false;
    boolean newestFirst = true;
    Button btnSave;
    Uri selectedImageUri;
    // Django Server URL

    String site_url = "https://ruabytes.pythonanywhere.com";

    JSONObject post_json;
    String imageUrl = null;
    Bitmap bmImg = null;
    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            selectedImageUri = uri;

                            Toast.makeText(
                                    this,
                                    "Image selected successfully!",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Connect Java with XML
        textView = findViewById(R.id.textView);
        btnLoad = findViewById(R.id.btn_load);
        btnSave = findViewById(R.id.btn_save);
        recyclerView = findViewById(R.id.recyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        btnSort = findViewById(R.id.btnSort);
        btnViewMode = findViewById(R.id.btnViewMode);
        btnViewMode = findViewById(R.id.btnViewMode);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        textView.setText("PhotoViewer Ready");
        btnViewMode.setOnClickListener(v -> {
            isGridView = !isGridView;

            if (isGridView) {
                recyclerView.setLayoutManager(
                        new GridLayoutManager(this, 2)
                );
                btnViewMode.setText("Switch to List View");
            } else {
                recyclerView.setLayoutManager(
                        new LinearLayoutManager(this)
                );
                btnViewMode.setText("Switch to Grid View");
            }
        });

        btnViewMode.setOnClickListener(v -> {
            isGridView = !isGridView;

            if (isGridView) {
                recyclerView.setLayoutManager(
                        new GridLayoutManager(this, 2)
                );
                btnViewMode.setText("Switch to List View");
            } else {
                recyclerView.setLayoutManager(
                        new LinearLayoutManager(this)
                );
                btnViewMode.setText("Switch to Grid View");
            }
        });
        btnSort.setOnClickListener(v -> {
            newestFirst = !newestFirst;

            if (newestFirst) {
                btnSort.setText("Sort: Newest First");
            } else {
                btnSort.setText("Sort: Oldest First");
            }
            searchPosts(searchEditText.getText().toString().trim());
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                searchPosts(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        btnSave.setOnClickListener(v -> {
            if (selectedImageUri == null) {
                imagePicker.launch("image/*");
            } else {
                showUploadDialog();
            }
        });


        // Sync Button
        btnLoad.setOnClickListener(v -> {

            textView.setText("Connecting to server...");

            // Background Thread
            new Thread(() -> {

                HttpURLConnection connection = null;

                try {
                    // Django REST API URL
                    URL url = new URL(
                            site_url + "/api_root/Post/"
                    );

                    // Open HTTP Connection
                    connection = (HttpURLConnection)
                            url.openConnection();

                    // GET Request
                    connection.setRequestMethod("GET");

                    // Timeouts
                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(15000);

                    // HTTP Response Code
                    int responseCode =
                            connection.getResponseCode();

                    if (responseCode ==
                            HttpURLConnection.HTTP_OK) {

                        // Read Server Response
                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                connection.getInputStream()
                                        )
                                );

                        StringBuilder response =
                                new StringBuilder();

                        String line;

                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        reader.close();

                        // Convert Response to String
                        String result = response.toString();

                        // Convert JSON String to JSONArray
                        JSONArray posts = new JSONArray(result);
                        allPosts = posts;

                        StringBuilder postTitles = new StringBuilder();

                        for (int i = 0; i < posts.length(); i++) {
                            JSONObject post = posts.getJSONObject(i);

                            String title = post.getString("title");
                            String description = post.optString("text", "");

                            postTitles.append(i + 1)
                                    .append(". ")
                                    .append(title)
                                    .append("\n")
                                    .append(description)
                                    .append("\n\n");
                        }

                        // Count Total Posts
                        int totalPosts = posts.length();

                        // Update Android UI

                        runOnUiThread(() -> {
                            textView.setText("Total Posts: " + totalPosts);
                            searchPosts(searchEditText.getText().toString().trim());
                        });


                    } else {

                        int statusCode = responseCode;

                        runOnUiThread(() -> {
                            textView.setText(
                                    "HTTP Error: " + statusCode
                            );
                        });
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                    String errorMessage = e.getMessage();

                    runOnUiThread(() -> {
                        textView.setText(
                                "Error: " + errorMessage
                        );
                    });

                } finally {

                    if (connection != null) {
                        connection.disconnect();
                    }
                }

            }).start();
        });

        // Existing Android Window Insets Code
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }
    private void searchPosts(String keyword) {
        ArrayList<JSONObject> matchedPosts = new ArrayList<>();

        for (int i = 0; i < allPosts.length(); i++) {
            JSONObject post = allPosts.optJSONObject(i);

            if (post == null) {
                continue;
            }

            String title = post.optString("title", "");

            if (title.toLowerCase().contains(keyword.toLowerCase())) {
                matchedPosts.add(post);
            }
        }

        Collections.sort(matchedPosts, (a, b) -> {
            String dateA = a.optString("created_date", "");
            String dateB = b.optString("created_date", "");

            if (newestFirst) {
                return dateB.compareTo(dateA);
            } else {
                return dateA.compareTo(dateB);
            }
        });

        JSONArray sortedPosts = new JSONArray();

        for (JSONObject post : matchedPosts) {
            sortedPosts.put(post);
        }

        PostAdapter adapter = new PostAdapter(sortedPosts);
        recyclerView.setAdapter(adapter);
    }




    private void showUploadDialog() {
        EditText titleInput = new EditText(this);
        titleInput.setHint("Post Title");

        EditText descriptionInput = new EditText(this);
        descriptionInput.setHint("Description");
        descriptionInput.setMinLines(2);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (20 * getResources()
                .getDisplayMetrics().density);
        layout.setPadding(padding, 8, padding, 8);

        layout.addView(titleInput);
        layout.addView(descriptionInput);

        new AlertDialog.Builder(this)
                .setTitle("Create New Post")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Upload", (dialog, which) -> {
                    String title = titleInput.getText()
                            .toString().trim();
                    String description = descriptionInput.getText()
                            .toString().trim();

                    if (title.isEmpty()) {
                        Toast.makeText(
                                this,
                                "Please enter a title",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    uploadImage(title, description);
                })
                .show();
    }


    private void uploadImage(String title, String description) {
        if (selectedImageUri == null) {
            Toast.makeText(
                    this,
                    "Please select an image first!",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }


        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(site_url + "/api_root/Post/");

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                String boundary = "PhotoViewerBoundary";

                connection.setRequestProperty(
                        "Content-Type",
                        "multipart/form-data; boundary=" + boundary
                );

                try (DataOutputStream output = new DataOutputStream(
                        connection.getOutputStream())) {


                    String fields =
                            "--" + boundary + "\r\n"
                                    + "Content-Disposition: form-data; name=\"author\"\r\n\r\n"
                                    + "1\r\n"
                                    + "--" + boundary + "\r\n"
                                    + "Content-Disposition: form-data; name=\"title\"\r\n\r\n"
                                    + title + "\r\n"
                                    + "--" + boundary + "\r\n"
                                    + "Content-Disposition: form-data; name=\"text\"\r\n\r\n"
                                    + description + "\r\n";

                    output.write(fields.getBytes(StandardCharsets.UTF_8));

                    output.write(("--" + boundary + "\r\n"
                            + "Content-Disposition: form-data; name=\"image\"; filename=\"photo.jpg\"\r\n"
                            + "Content-Type: image/jpeg\r\n\r\n")
                            .getBytes(StandardCharsets.UTF_8));

                    try (InputStream input =
                                 getContentResolver().openInputStream(selectedImageUri)) {

                        if (input == null) {
                            throw new java.io.IOException("Cannot open selected image");
                        }

                        byte[] buffer = new byte[8192];
                        int bytesRead;

                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                    }

                    output.write(("\r\n--" + boundary + "--\r\n")
                            .getBytes(StandardCharsets.UTF_8));
                    output.flush();
                }

                int responseCode = connection.getResponseCode();

                runOnUiThread(() -> {
                    Toast.makeText(
                            this,
                            "HTTP Response: " + responseCode,
                            Toast.LENGTH_LONG
                    ).show();
                });

            } catch (Exception e) {
                e.printStackTrace();

                runOnUiThread(() -> {
                    Toast.makeText(
                            this,
                            "Upload connection error",
                            Toast.LENGTH_LONG
                    ).show();
                });

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();

    }

}

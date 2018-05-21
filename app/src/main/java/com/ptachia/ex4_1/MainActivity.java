package com.ptachia.ex4_1;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private List<Photo> photos = new ArrayList<>();

    public List<Photo> getPhotos() {
        return photos;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button set_album = findViewById(R.id.show);
        set_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchData();
            }
        });
    }

    private void fetchData(){
        OkHttpClient httpClient;
        httpClient = new OkHttpClient.Builder().build();

        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/album/M7HqyVC/images")
                .header("Authorization", "Client-ID d754ae180941bbb")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Failure error: ", e.getMessage());
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try{
                    assert response.body() != null;
                    JSONObject data = new JSONObject(Objects.requireNonNull(response.body()).string());
                    JSONArray items = data.getJSONArray("data");
                    for (int i = 0; i < items.length(); i++){
                        JSONObject item = items.getJSONObject(i);
                        Photo photo = new Photo(item.getString("id"));
                        photos.add(photo);
                    }
                    runOnUiThread(new Runnable() { // check that its runs on the UI thread
                        @Override
                        public void run() {
                            set_rv();
                        }
                    });
                } catch (Exception e){
                    Log.e("Response error: ", e.getMessage());
                }
            }
        });
    }

    private void set_rv(){
        RecyclerView rv = findViewById(R.id.my_grid);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        Adapter mAdapter = new Adapter(this);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(mAdapter);
    }

//*************************************Adapter class************************************************

    public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        private MainActivity mMain;

        Adapter(MainActivity mMain) {
            this.mMain = mMain;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Picasso.get()
                    .load("https://i.imgur.com/" + mMain.getPhotos().get(position).id + ".jpg")
                    .into(holder.photo);
        }

        @Override
        public int getItemCount() {
            return mMain.getPhotos().size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            ImageView photo;
            MyViewHolder(View view) {
                super(view);
                photo = view.findViewById(R.id.iv);
            }
        }
    }

//**************************************Photo class*************************************************

    public class Photo {
        String id;
        Photo(String id) {
            this.id = id;
        }
    }

}

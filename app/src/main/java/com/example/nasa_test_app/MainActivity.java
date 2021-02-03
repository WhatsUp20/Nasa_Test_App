package com.example.nasa_test_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.nasa_test_app.adapter.NasaAdapter;
import com.example.nasa_test_app.api.NetworkApi;
import com.example.nasa_test_app.api.NetworkService;
import com.example.nasa_test_app.data.Datum;
import com.example.nasa_test_app.data.Item;
import com.example.nasa_test_app.data.Link;
import com.example.nasa_test_app.data.ObjectCollection;

import java.util.ArrayList;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private NasaAdapter adapter;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Switch switch1 = findViewById(R.id.switch1);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new NasaAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        switch1.setChecked(true);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loadMarsData();
                } else {
                    loadSpaceData();
                }
            }
        });
        switch1.setChecked(false);
    }

    private void imageClickListener() {
        adapter.setOnImageClickListener(position -> {
            Link link1 = adapter.getLinkList().get(position);
            Datum datum1 = adapter.getDatumList().get(position);
            Intent intent = new Intent(MainActivity.this, NasaDetailActivity.class);
            intent.putExtra("content", link1.getHref());
            intent.putExtra("title", datum1.getTitle());
            intent.putExtra("desc", datum1.getDescription());
            MainActivity.this.startActivity(intent);
        });
    }

    private void loadSpaceData() {
        compositeDisposable = new CompositeDisposable();
        NetworkService service = NetworkService.getInstance();
        final NetworkApi api = service.getAllApi();
        Disposable disposable = api.getAllSpaceCollections()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<ObjectCollection, Throwable>() {
                    @Override
                    public void accept(ObjectCollection objectCollection, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(MainActivity.this, "Data loading error " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            //Get All lists
                            List<List<Link>> link = new ArrayList<>();
                            List<List<Datum>> datum = new ArrayList<>();
                            List<Link> linkList = new ArrayList<>();
                            List<Datum> datumList = new ArrayList<>();
                            List<Item> items = objectCollection.getCollection().getItems();

                            //Fill link and datum lists data from item list
                            for (int i = 0; i < items.size(); i++) {
                                link.add(objectCollection.getCollection().getItems().get(i).getLinks());
                                datum.add(objectCollection.getCollection().getItems().get(i).getData());
                            }
                            //Get and add all dates to linkList
                            for (int i = 0; i < link.size(); i++) {
                                List<Link> links = link.get(i);
                                linkList.addAll(links);
                            }
                            //Get and add all dates to datumList
                            for (int i = 0; i < datum.size(); i++) {
                                List<Datum> datumList1 = datum.get(i);
                                datumList.addAll(datumList1);
                            }
                            adapter.setLinkList(linkList);
                            adapter.setDatumList(datumList);

                           imageClickListener();
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void loadMarsData() {
        compositeDisposable = new CompositeDisposable();
        NetworkService service = NetworkService.getInstance();
        final NetworkApi api = service.getAllApi();
        Disposable disposable = api.getAllMarsCollection()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<ObjectCollection, Throwable>() {
                    @Override
                    public void accept(ObjectCollection objectCollection, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(MainActivity.this, "Data loading error " + throwable, Toast.LENGTH_SHORT).show();
                        } else {
                            //Get All lists
                            List<List<Link>> link = new ArrayList<>();
                            List<List<Datum>> datum = new ArrayList<>();
                            List<Link> linkList = new ArrayList<>();
                            List<Datum> datumList = new ArrayList<>();
                            List<Item> items = objectCollection.getCollection().getItems();

                            //Fill link and datum lists data from item list
                            for (int i = 0; i < items.size(); i++) {
                                link.add(objectCollection.getCollection().getItems().get(i).getLinks());
                                datum.add(objectCollection.getCollection().getItems().get(i).getData());
                            }
                            //Get and add all dates to linkList
                            for (int i = 0; i < link.size(); i++) {
                                List<Link> links = link.get(i);
                                linkList.addAll(links);
                            }
                            //Get and add all dates to datumList
                            for (int i = 0; i < datum.size(); i++) {
                                List<Datum> datumList1 = datum.get(i);
                                datumList.addAll(datumList1);
                            }
                            adapter.setLinkList(linkList);
                            adapter.setDatumList(datumList);

                            imageClickListener();
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }
}
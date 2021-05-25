package com.vedantsuram.newsgateway;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Fragment extends androidx.fragment.app.Fragment implements Serializable {

    public Fragment() {

    }

    public static Fragment newInstance(NewsStoryObject newsStoryObject) {
        Fragment fragment = new Fragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("article", newsStoryObject);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        assert getArguments() != null;
        outState.putSerializable("article", getArguments().getSerializable("article"));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final NewsStoryObject newsStoryObject;
        if (savedInstanceState == null) {
            assert getArguments() != null;
            newsStoryObject = (NewsStoryObject) getArguments().getSerializable("article");
        }
        else
            newsStoryObject = (NewsStoryObject) savedInstanceState.getSerializable("article");
        View view = inflater.inflate(R.layout.fragment_article, container, false);

        TextView titleTextView = view.findViewById(R.id.titleView);
        TextView authorTexView =  view.findViewById(R.id.authorView);
        TextView dateTextView =  view.findViewById(R.id.dateView);
        TextView descriptionTextView =  view.findViewById(R.id.descriptionView);
        TextView indexTextView = view.findViewById(R.id.indexView);
        final ImageButton imageButton =  view.findViewById(R.id.imageView);

        assert newsStoryObject != null;
        titleTextView.setText(newsStoryObject.getTitle());
        String author= newsStoryObject.getAuthor();
        if(author.equalsIgnoreCase("null"))
            authorTexView.setText("");
        else
            authorTexView.setText(newsStoryObject.getAuthor());

        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat formatOutput = new SimpleDateFormat("MMM dd, yyyy hh:mm");

        Date date = null;
        String str = null;

        try {
            date = formatInput.parse(newsStoryObject.getPublishedAt());
            str = formatOutput.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert str != null;
        if(str.equalsIgnoreCase("null"))
            dateTextView.setText("");
        else
            dateTextView.setText(str);

        descriptionTextView.setText(newsStoryObject.getDescription());
        indexTextView.setText(""+ newsStoryObject.getIndex()+" of "+(newsStoryObject.getTotal()-90));

        if (newsStoryObject.getUrlToImage() != null){
            Picasso picasso = new Picasso.Builder(view.getContext()).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                    final String change = newsStoryObject.getUrlToImage().replace("http:", "https:");
                    picasso.load(change) .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder) .into(imageButton);
                }
            }).build();

            picasso.load(newsStoryObject.getUrlToImage()) .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder) .into(imageButton);
        } else {
            Picasso.get().load(newsStoryObject.getUrlToImage()).error(R.drawable.brokenimage).placeholder(R.drawable.missingimage);
        }

        final Intent i = new Intent((Intent.ACTION_VIEW));
        i.setData(Uri.parse(newsStoryObject.getUrl()));
        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });
        descriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

}

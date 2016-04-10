package io.macromind.fyp_hkust;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.ImageView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

public class ResultInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultinfo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String food = getIntent().getStringExtra("EXTRA_FOOD");
//        Toast.makeText(this, "info_".concat(food).toString(), Toast.LENGTH_LONG).show();

        ImageView image = (ImageView) findViewById(R.id.imageView);
        image.setImageResource(getResources().getIdentifier("f".concat(food), "drawable", ResultInfoActivity.this.getPackageName()));
//        image.setBackground(getDrawable(getResources().getIdentifier("f".concat(food), "drawable", ResultInfoActivity.this.getPackageName())));

        int res = getResources().getIdentifier("info_".concat(food), "string", ResultInfoActivity.this.getPackageName());
        if(res!=0) {
            ViewStub stub = (ViewStub) findViewById(R.id.expand_text_view_description);
            stub.setLayoutResource(R.layout.expandable_text);
            View view = stub.inflate();
            ((TextView) view.findViewById(R.id.title)).setText("More Description");
            ExpandableTextView description = (ExpandableTextView) view.findViewById(R.id.expand_text_view);
            description.setText(getString(res));
        }

        res = getResources().getIdentifier("similar_".concat(food), "string", ResultInfoActivity.this.getPackageName());
        if(res!=0) {
            ViewStub stub = (ViewStub) findViewById(R.id.expand_text_view_similarFood);
            stub.setLayoutResource(R.layout.expandable_text);
            View view = stub.inflate();
            ((TextView) view.findViewById(R.id.title)).setText("Similar Food Elsewhere");
            ExpandableTextView similarFood = (ExpandableTextView) view.findViewById(R.id.expand_text_view);
            similarFood.setText(getString(res));
        }

        res = getResources().getIdentifier("where_".concat(food), "string", ResultInfoActivity.this.getPackageName());
        if(res!=0) {
            ViewStub stub = (ViewStub) findViewById(R.id.expand_text_view_whereToFind);
            stub.setLayoutResource(R.layout.expandable_text);
            View view = stub.inflate();
            ((TextView) view.findViewById(R.id.title)).setText("Where else to Find it");
            ExpandableTextView whereToFind = (ExpandableTextView) view.findViewById(R.id.expand_text_view);
            whereToFind.setText(getString(res));
        }

        res = getResources().getIdentifier("recipe_".concat(food), "string", ResultInfoActivity.this.getPackageName());
        if(res!=0) {
            ViewStub stub = (ViewStub) findViewById(R.id.expand_text_view_recipe);
            stub.setLayoutResource(R.layout.expandable_text);
            View view = stub.inflate();
            ((TextView) view.findViewById(R.id.title)).setText("How to make it");
            ExpandableTextView recipe = (ExpandableTextView) view.findViewById(R.id.expand_text_view);
            recipe.setText(getString(res));
        }

    }



}

package io.macromind.fyp_hkust;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.Arrays;

/**
 * Created by Justin on 16/4/5.
 */
public class ResultActivity extends AppCompatActivity{

	private int[] mResults;
    private String mPath;
    private Bitmap mBitmap;
    private ImageView mCapturedImage;
	private ListView mResultsListView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCapturedImage = (ImageView) findViewById(R.id.captured_image);
        mResultsListView = (ListView) findViewById(R.id.results_list);
        mResults = getIntent().getIntArrayExtra(MainActivity.EXTRA_RESULT_ARRAY);
        mPath = getIntent().getStringExtra(MainActivity.EXTRA_IMAGE_PATH);

//        Log.i("Returned result 1 is.", new Integer(mResults[0]).toString());
//        Log.i("Returned result 1 is.", mResults[0]-1 >= 0 ? MainActivity.DIM_SUM_CLASSES[mResults[0]-1] : MainActivity.DIM_SUM_CLASSES[6]);
//        Log.i("Returned result 1 is.", new Integer(mResults[1]).toString());
//        Log.i("Returned result 2 is.", mResults[1]-1 >= 0 ? MainActivity.DIM_SUM_CLASSES[mResults[1]-1] : MainActivity.DIM_SUM_CLASSES[6]);
//        Log.i("Returned result 1 is.", new Integer(mResults[2]).toString());
//        Log.i("Returned result 3 is.", mResults[2]-1 >= 0 ? MainActivity.DIM_SUM_CLASSES[mResults[2]-1] : MainActivity.DIM_SUM_CLASSES[6]);

        mBitmap = BitmapFactory.decodeFile(mPath);
//        mBitmap = Bitmap.createScaledBitmap(mBitmap, 500, 500*mBitmap.getHeight()/mBitmap.getWidth(), true);
        mCapturedImage.setImageBitmap(mBitmap);


        String[] resultArray = new String[mResults.length];
        int i = 0;
        for (int j: mResults) {
            //resultArray[i++] = MainActivity.DIM_SUM_CLASSES[j];
            resultArray[i++] =j-1 >= 0 ? MainActivity.DIM_SUM_CLASSES[j-1] : MainActivity.DIM_SUM_CLASSES[6];
        }
        adapter = new ArrayAdapter<String>(this, R.layout.result_list_item, resultArray);
        mResultsListView.setAdapter(adapter);

        mResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String foodName = adapter.getItem(position);
                Log.i("CLASSDD", foodName);
                Log.i("CLASSDD", MainActivity.DIM_SUM_INDEXES[Arrays.asList(MainActivity.DIM_SUM_CLASSES).indexOf(foodName)].toString());
//

                Intent activityChangeIntent = new Intent(ResultActivity.this, ResultInfoActivity.class);

                activityChangeIntent.putExtra("EXTRA_FOOD", MainActivity.DIM_SUM_INDEXES[Arrays.asList(MainActivity.DIM_SUM_CLASSES).indexOf(foodName)].toString());
                ResultActivity.this.startActivity(activityChangeIntent);

            }
        });
    }


}

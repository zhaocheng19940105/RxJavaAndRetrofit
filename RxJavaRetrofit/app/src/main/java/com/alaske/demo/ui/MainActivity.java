package com.alaske.demo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.alaske.demo.App;
import com.alaske.demo.R;
import com.alaske.demo.api.WebApiService;
import com.alaske.demo.mvp.mo.UserMo;
import com.alaske.demo.mvp.presenter.user.IUserPresenter;
import com.alaske.demo.mvp.presenter.user.UserPresenterImpl;

import java.io.IOException;

import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifDrawable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements IUserView {

    private static String LOG_TAG = MainActivity.class.getName();
    IUserPresenter mIUserPresenter;
    ImageView image_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image_view = (ImageView) findViewById(R.id.image_view);
        mIUserPresenter = new UserPresenterImpl(this);
//        mIUserPresenter.getUser("");
        //demo 接口请求 是否调通
        Call<ResponseBody> call = App.apiService(WebApiService.class).getUrl("public/user_data/images/20150408/topic/05d2275fcf!");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    GifDrawable drawable = new GifDrawable(response.body().bytes());
                    image_view.setImageDrawable(drawable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(LOG_TAG, "onFailure  " + t.toString());
            }
        });
    }


    @Override
    public void updateView(UserMo user) {

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void showError(String msg) {

    }
}

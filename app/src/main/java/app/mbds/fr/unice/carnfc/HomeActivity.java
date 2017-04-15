package app.mbds.fr.unice.carnfc;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;

import app.mbds.fr.unice.carnfc.map.MapGeolocation;

public class HomeActivity extends Activity implements OnMapReadyCallback, View.OnClickListener{

    private static final String TAG = "HomeActivity";

    private MapGeolocation mapGeolocation;

    //Animators
    List<AnimatorSet> animators = new ArrayList<>();
    List<AnimatorSet> animatorsReverse = new ArrayList<>();
    private boolean menuIsOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Init map
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initViews();
    }

    private void initViews(){
        //Init Listeners
        ImageView menuBtn = (ImageView)findViewById(R.id.btn_menu);
        menuBtn.setOnClickListener(this);

        ImageView locationBtn = (ImageView)findViewById(R.id.btn_car_location);
        locationBtn.setOnClickListener(this);

        ImageView parkingBtn = (ImageView)findViewById(R.id.btn_parking);
        parkingBtn.setOnClickListener(this);

        ImageView accountBtn = (ImageView)findViewById(R.id.btn_account);
        accountBtn.setOnClickListener(this);

        //Animations
        loadAnimators(locationBtn, R.anim.item_menu_location_animator);
        loadAnimators(parkingBtn, R.anim.item_menu_parking_animator);
        loadAnimators(accountBtn, R.anim.item_menu_account_animator);
    }

    private void loadAnimators(View component, int id){
        AnimatorSet animator = (AnimatorSet) AnimatorInflater.loadAnimator(this, id);
        animator.setTarget(component);
        animators.add(animator);

        AnimatorSet animatorRev = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                R.anim.item_menu_reverse_animator);
        animatorRev.setTarget(component);
        animatorsReverse.add(animatorRev);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (!checkPermission()) {
            Log.i(TAG, "Permission location denied...");
            return;
        }

        mapGeolocation = new MapGeolocation(this, googleMap);
        mapGeolocation.start();
    }


    private boolean checkPermission(){
        return  ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(mapGeolocation != null){
            mapGeolocation.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mapGeolocation != null){
            mapGeolocation.stop();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_menu:
                animateMenus();
                break;
            case R.id.btn_car_location:
                if(mapGeolocation != null){
                    mapGeolocation.activeModeSearchCar();
                }
                break;
            case R.id.btn_parking:
                if(mapGeolocation != null){
                    mapGeolocation.activeModeSearchParking();
                }
                break;
            case R.id.btn_account:
                Toast.makeText(this, "account", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void animateMenus(){
        if(!menuIsOpen){
            for(AnimatorSet set : animators){
                set.start();
            }
        }else{
            for(AnimatorSet set : animatorsReverse){
                set.start();
            }
        }

        menuIsOpen = !menuIsOpen;
    }

}

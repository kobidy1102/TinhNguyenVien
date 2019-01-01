package com.example.pc_asus.tinhnguyenvien;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pc_asus.tinhnguyenvien.FindDirection.DirectionFinder;
import com.example.pc_asus.tinhnguyenvien.FindDirection.DirectionFinderListener;
import com.example.pc_asus.tinhnguyenvien.FindDirection.Route;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MapViewFragment extends Fragment implements OnMapReadyCallback {
    public static GoogleMap mMap;
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    public List<Marker> originMarkers = new ArrayList<>();
    public List<Marker> destinationMarkers = new ArrayList<>();
    public List<Polyline> polylinePaths = new ArrayList<>();
    public ProgressDialog progressDialog;

    Button btnMore ;
    View view;
    Marker marker = null;
    double longi, lati;
    ListView lvFriends;
    int showListPlace;
    DirectionA d= new DirectionA();

    String key="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_map_view, container, false);


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        key= VideoCallAndMapActivity.key;
        Log.e("abc","key map Fragment "+key);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = mCurrentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //TODO
     //   CheckConnectionService.keyRoomVideoChat = "D79LimcFQNOkz1gVuok3lQtQDhy1";

        mDatabase.child("NguoiMu").child("Location").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("abc",key + " aaaa");
                String latitude = dataSnapshot.child("latitude").getValue().toString();
                String longitude = dataSnapshot.child("longitude").getValue().toString();
                int direction = Integer.parseInt(dataSnapshot.child("direction").getValue().toString());

                lati = Double.parseDouble(latitude);
                longi = Double.parseDouble(longitude);
                try{
                Bitmap bmp;


                    switch (direction) {

                            case 1:
                                bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca1);
                                break;
                            case 2:
                                bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca2);
                                break;
                            case 3:
                                bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca3);
                                break;
                            case 4:
                                bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca4);
                                break;
                            case 5:
                                bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca5);
                                break;
                            case 6:
                                bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca6);
                                break;
                            case 7:
                                bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca7);
                                break;
                            case 8:
                                bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca8);
                                break;
                            default:
                                bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca7);
                                break;


                    }

                try {
                    marker.remove();


                } catch (Exception e) { }
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude),
                        Double.parseDouble(longitude))).icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bmp, 65, 65, false))));
            }catch (Exception e){Log.e("abc","lỗi:"+e);}


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ImageView img_location = view.findViewById(R.id.img_current_location);
      //  edtEndAddress = view.findViewById(R.id.edt_endAddress);
        btnMore = view.findViewById(R.id.btn_more);
        lvFriends= view.findViewById(R.id.lv_friends);
        lvFriends.setVisibility(View.INVISIBLE);


        img_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati, longi), 14));

            }
        });


        final ArrayList<String> arrFriends= new ArrayList<String>();
        final ArrayList<String> arrAddressFriend= new ArrayList<String>();

        showListPlace=0;
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListPlace++;
                if(showListPlace%2==0){
                    lvFriends.setVisibility(View.INVISIBLE);
                }else {
                    lvFriends.setVisibility(View.VISIBLE);
                }

                final ArrayAdapter adapter= new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,arrFriends);
                lvFriends.setAdapter(adapter);
                arrFriends.clear();


                mDatabase.child("NguoiMu").child("PlacesOftenCome").child(key).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        arrFriends.add(dataSnapshot.child("namePlace").getValue().toString());
                        arrAddressFriend.add(dataSnapshot.child("address").getValue().toString());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });



        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String startAddress = lati + "," + longi;
                String endAddress= arrAddressFriend.get(position);
                try {
                    new DirectionFinder(d, startAddress, endAddress).execute();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Không tìm thấy tuyến đường", Toast.LENGTH_SHORT).show();

                }
                lvFriends.setVisibility(View.INVISIBLE);
            }
        });




        PlaceAutocompleteFragment autocompleteFragment1= (PlaceAutocompleteFragment)getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment1);

        autocompleteFragment1.getView().setBackgroundColor(Color.WHITE);

        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Log.e("abc", "Place: " + place.getName());
                String startAddress = lati + "," + longi;
                String endAddress= place.getAddress().toString();

                try {
                    new DirectionFinder(d, startAddress, endAddress).execute();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Không tìm thấy tuyến đường", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(com.google.android.gms.common.api.Status status) {
                Log.e("abc", "An error occurred: " + status);
            }


        });





        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13, 107.5), 6));

    }





    class DirectionA implements DirectionFinderListener {

        @Override
        public void onDirectionFinderStart() {
            progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
                    "Finding direction..!", true);

            if (originMarkers != null) {
                for (Marker marker : originMarkers) {
                    marker.remove();
                }
            }

            if (destinationMarkers != null) {
                for (Marker marker : destinationMarkers) {
                    marker.remove();
                }
            }

            if (polylinePaths != null) {
                for (Polyline polyline : polylinePaths) {
                    polyline.remove();
                }
            }
        }

        @Override
        public void onDirectionFinderSuccess(List<Route> routes) {
            progressDialog.dismiss();
            polylinePaths = new ArrayList<>();
            originMarkers = new ArrayList<>();
            destinationMarkers = new ArrayList<>();

            for (Route route : routes) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 14));

                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(route.endAddress)
                        .position(route.endLocation)));

                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.BLUE).
                        width(10);

                for (int i = 0; i < route.points.size(); i++)
                    polylineOptions.add(route.points.get(i));

                polylinePaths.add(mMap.addPolyline(polylineOptions));
            }
        }
    }
}
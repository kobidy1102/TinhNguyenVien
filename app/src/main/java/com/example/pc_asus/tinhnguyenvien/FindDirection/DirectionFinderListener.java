package com.example.pc_asus.tinhnguyenvien.FindDirection;

import java.util.List;


public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}

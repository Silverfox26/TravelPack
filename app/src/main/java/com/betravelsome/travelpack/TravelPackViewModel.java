package com.betravelsome.travelpack;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.betravelsome.travelpack.data.TravelPackRepository;

public class TravelPackViewModel extends AndroidViewModel {

    // Member variable declarations
    private final TravelPackRepository mRepository;


    public TravelPackViewModel(Application application) {
        super(application);
        mRepository = new TravelPackRepository(application);
    }
}

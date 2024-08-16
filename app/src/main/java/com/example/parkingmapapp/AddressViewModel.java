package com.example.parkingmapapp;

import androidx.lifecycle.MutableLiveData;

public class AddressViewModel
{
    AddressRepository addressRepository;
    public AddressViewModel()
    {
        addressRepository = new AddressRepository();
    }

    public MutableLiveData<Address> getAddressVM(String geoPoint, String apiKey)
    {
        return addressRepository.getAddress(geoPoint, apiKey);
    }
}

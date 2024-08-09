package com.example.parkingmapapp;

import androidx.lifecycle.MutableLiveData;

public class AddressViewModel
{
    AddressRepository addressRepository;
    public AddressViewModel()
    {
        addressRepository = new AddressRepository();
    }

    public MutableLiveData<Address> getAddressVM(double lat, double lon, String format)
    {
        return addressRepository.getAddress(lat, lon, format);
    }
}

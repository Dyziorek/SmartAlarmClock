package com.example.smartalarmclock.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

public class DashBoardViewModelFactory implements ViewModelProvider.Factory {
    private DashboardRepository repoData;

    public DashBoardViewModelFactory(DashboardRepository repository)
    {
        repoData = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new DashboardViewModel(repoData);
    }
}

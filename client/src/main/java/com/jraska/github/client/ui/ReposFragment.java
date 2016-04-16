package com.jraska.github.client.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jraska.github.client.R;
import com.jraska.github.client.users.Repo;

import java.util.List;

public class ReposFragment extends Fragment {
  @Bind(R.id.repos_recycler) RecyclerView _reposRecyclerView;
  @Bind(R.id.repos_container) ViewGroup _reposContainer;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_repos, container, false);
    ButterKnife.bind(this, view);

    setContainerVisibility(false);
    _reposRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    return view;
  }

  void setRepos(List<Repo> repos) {
    if (_reposRecyclerView == null) {
      throw new IllegalStateException("View was not created yet");
    }

    setContainerVisibility(repos.size() > 0);

    ReposAdapter reposAdapter = new ReposAdapter(LayoutInflater.from(getActivity()));
    reposAdapter.setRepos(repos);
    _reposRecyclerView.setAdapter(reposAdapter);
  }

  private void setContainerVisibility(boolean visible) {
    if (visible) {
      _reposContainer.setVisibility(View.VISIBLE);
    } else {
      _reposContainer.setVisibility(View.GONE);
    }
  }
}
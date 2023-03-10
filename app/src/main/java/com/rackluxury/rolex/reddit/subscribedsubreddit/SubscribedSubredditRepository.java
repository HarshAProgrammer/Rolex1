package com.rackluxury.rolex.reddit.subscribedsubreddit;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.rackluxury.rolex.reddit.RedditDataRoomDatabase;

public class SubscribedSubredditRepository {
    private SubscribedSubredditDao mSubscribedSubredditDao;
    private LiveData<List<SubscribedSubredditData>> mAllSubscribedSubreddits;
    private LiveData<List<SubscribedSubredditData>> mAllFavoriteSubscribedSubreddits;

    SubscribedSubredditRepository(RedditDataRoomDatabase redditDataRoomDatabase, String accountName) {
        mSubscribedSubredditDao = redditDataRoomDatabase.subscribedSubredditDao();
        mAllSubscribedSubreddits = mSubscribedSubredditDao.getAllSubscribedSubreddits(accountName);
        mAllFavoriteSubscribedSubreddits = mSubscribedSubredditDao.getAllFavoriteSubscribedSubreddits(accountName);
    }

    LiveData<List<SubscribedSubredditData>> getAllSubscribedSubreddits() {
        return mAllSubscribedSubreddits;
    }

    public LiveData<List<SubscribedSubredditData>> getAllFavoriteSubscribedSubreddits() {
        return mAllFavoriteSubscribedSubreddits;
    }

    public void insert(SubscribedSubredditData subscribedSubredditData) {
        new insertAsyncTask(mSubscribedSubredditDao).execute(subscribedSubredditData);
    }

    private static class insertAsyncTask extends AsyncTask<SubscribedSubredditData, Void, Void> {

        private SubscribedSubredditDao mAsyncTaskDao;

        insertAsyncTask(SubscribedSubredditDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final SubscribedSubredditData... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}

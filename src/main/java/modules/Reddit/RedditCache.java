package modules.Reddit;

import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.pagination.DefaultPaginator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RedditCache {

    private static final int SEARCH_LIMIT = 100;
    private static final SubredditSort SEARCH_SORT = SubredditSort.TOP;

    private String subredditName;
    private TimePeriod timePeriod;

    private List<Submission> submissions;

    private Instant expiration;

    public RedditCache(String subredditName, TimePeriod timePeriod) {
        this.subredditName = subredditName;
        this.timePeriod = timePeriod;

        this.expiration = Instant.now().plus(12, ChronoUnit.HOURS);

        this.submissions = new ArrayList<>();

        updateCache();
    }

    public String getSubredditName() {
        return subredditName;
    }

    public TimePeriod getTimePeriod() {
        return timePeriod;
    }

    public List<Submission> getSubmissions() {
        if(isExpired())
            updateCache();
        return submissions;
    }

    public Submission getRandomSubmission() {
        if(getSubmissions().size() < 1)
            updateCache();
        Submission randomSubmission = getSubmissions().get(new Random().nextInt(submissions.size()));
        submissions.remove(randomSubmission);
        return randomSubmission;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public boolean isExpired() {
        return expiration.isBefore(Instant.now());
    }


    public void updateCache() {
        submissions.clear();

        DefaultPaginator<Submission> aggregator;
        DefaultPaginator.Builder<Submission, SubredditSort> builder;

        builder = Reddit.reddit.subreddit(subredditName).posts();
        aggregator = builder
                .limit(SEARCH_LIMIT)
                .sorting(SEARCH_SORT)
                .timePeriod(timePeriod)
                .build();

        //If the post is not a self post, cache it
        aggregator.next().stream()
                .filter(post -> !post.isSelfPost())
                .filter(post -> post.getDomain().contains("imgur.com")
                        || post.getDomain().contains("i.redd.it")
                        || post.getDomain().contains("i.redditmedia.com")
                        || post.getDomain().contains("gfycat.com"))
                .forEach(submissions::add);
    }

}

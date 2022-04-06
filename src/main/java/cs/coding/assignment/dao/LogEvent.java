package cs.coding.assignment.dao;

import java.util.Objects;
import java.util.StringJoiner;

public class LogEvent {

    private String id, state, type, host;
    private long timestamp, duration;

    public String getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public String getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state);
    }

    @Override
    public String toString() {
        StringJoiner sj =
        new StringJoiner(", ", LogEvent.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("duration=" + duration)
                .add("alert='" + (duration > 4) + "'");

        return type == null ? sj.toString() : sj.add("type='" + type + "'")
                .add("host='" + host + "'")
                .toString();
    }
}

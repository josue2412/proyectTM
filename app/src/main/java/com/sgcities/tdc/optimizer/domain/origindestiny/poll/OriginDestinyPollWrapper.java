package com.sgcities.tdc.optimizer.domain.origindestiny.poll;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.sgcities.tdc.optimizer.domain.origindestiny.answer.OriginDestinyPollAnswer;
import com.sgcities.tdc.optimizer.domain.shared.Storable;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OriginDestinyPollWrapper implements Storable{

    @Embedded
    private OriginDestinyPoll poll;

    @Relation(parentColumn = "id", entityColumn = "poll_id", entity = OriginDestinyPollAnswer.class)
    private List<OriginDestinyPollAnswer> answers;

    public int getBackedUpRemotely() {
        return poll.getBackedUpRemotely();
    }

    @Override
    public void setBackedUpRemotely(int value) {
        poll.setBackedUpRemotely(value);
    }

    @Override
    public int getId() {
        return (int)poll.getId();
    }
}

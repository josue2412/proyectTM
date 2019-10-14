package com.sgcities.tdc.optimizer.domain.busroute;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity(tableName = "BusRoute")
public class BusRoute {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "routeName")
    private String routeName;

    @ColumnInfo(name = "routeVia")
    private String routeVia;

    @Override
    public String toString() {
        StringBuilder toStringValue = new StringBuilder();
        toStringValue.append(this.routeName);
        if (this.routeVia != null) {
            if (!this.routeVia.isEmpty()) {
                toStringValue.append(" ");
                toStringValue.append(this.routeVia);
            }
        }
        return toStringValue.toString();
    }
}

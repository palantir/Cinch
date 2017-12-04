package com.palantir.ptoss.cinch;

import com.palantir.ptoss.cinch.core.DefaultBindableModel;

import java.io.Serializable;

public class SerializableBindableModel extends DefaultBindableModel implements Serializable {

    private final String data;

    public SerializableBindableModel(String data) {
        super();
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "SerializableBindableModel{" +
                "data='" + data + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SerializableBindableModel that = (SerializableBindableModel) o;

        return data != null ? data.equals(that.data) : that.data == null;
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }

}

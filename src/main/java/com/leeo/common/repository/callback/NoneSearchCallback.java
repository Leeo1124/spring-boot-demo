package com.leeo.common.repository.callback;

import javax.persistence.Query;

import com.leeo.common.entity.search.Searchable;

public final class NoneSearchCallback implements SearchCallback {

    @Override
    public void prepareQL(StringBuilder ql, Searchable search) {
    }

    @Override
    public void prepareOrder(StringBuilder ql, Searchable search) {
    }

    @Override
    public void setValues(Query query, Searchable search) {
    }

    @Override
    public void setPageable(Query query, Searchable search) {
    }
}

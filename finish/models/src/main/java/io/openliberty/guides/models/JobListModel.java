// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.models;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class JobListModel {
    private List<JobResultModel> results;
    private int count;
    private OptionalDouble averageResult;

    public JobListModel() {
        results = new ArrayList<>();
        setCount(results.size());
        setAverageResult(OptionalDouble.empty());
    }

    public JobListModel(List<JobResultModel> results) {
        this.results = results;
        setCount(results.size());
        setAverageResult(
            results
            .stream()
            .mapToInt(r -> r.getResult())
            .average());
   }

    public List<JobResultModel> getResults() {
        return results;
    }

    public void setResults(List<JobResultModel> results) {
        this.results = results;
    }

    public OptionalDouble getAverageResult() {
        return averageResult;
    }

    public void setAverageResult(OptionalDouble averageResult) {
        this.averageResult = averageResult;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}

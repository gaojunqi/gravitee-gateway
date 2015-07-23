/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.gateway.core.policy.impl;

import io.gravitee.gateway.api.Request;
import io.gravitee.gateway.api.Response;
import io.gravitee.gateway.api.policy.PolicyChain;
import io.gravitee.gateway.core.policy.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ListIterator;

/**
 * @author David BRASSELY (brasseld at gmail.com)
 */
public class RequestPolicyChain implements PolicyChain {

    private final Logger LOGGER = LoggerFactory.getLogger(RequestPolicyChain.class);

    private final List<Policy> policies;

    public RequestPolicyChain(final List<Policy> policies) {
        if (policies == null) {
            throw new IllegalArgumentException("List of policies can't be null.");
        }

        this.policies = policies;
    }

    @Override
    public void doNext(final Request request, final Response response) {
        if (iterator().hasNext()) {
            Policy policy = iterator().next();
            try {
                policy.onRequest(request, response, this);
            } catch (Exception ex) {
                LOGGER.error("Unexpected error while running onRequest on policy", ex);
                doNext(request, response);
            }
        }
    }

    @Override
    public void doError(Throwable throwable) {

    }

    public ListIterator<Policy> iterator() {
        return policies.listIterator();
    }
}
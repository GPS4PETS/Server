/*
 * Copyright 2016 - 2024 Anton Tananaev (anton@traccar.org)
 * Copyright 2016 - 2017 Andrey Kunitsyn (andrey@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.handler;

import jakarta.inject.Inject;
import org.traccar.config.Config;
import org.traccar.model.Position;
import org.traccar.session.cache.CacheManager;

public class CopyNetworkHandler extends BasePositionHandler {

    private final CacheManager cacheManager;

    @Inject
    public CopyNetworkHandler(Config config, CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void onPosition(Position position, Callback callback) {
        Position last = cacheManager.getPosition(position.getDeviceId());
        if (last != null) {
            if (last.getNetwork() != null && position.getNetwork() == null) {
                position.setNetwork(last.getNetwork());
            }
        }
        callback.processed(false);
    }

}



/*
 *  Copyright (C) 2019 Alpha Jiang. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.aj.hyena.utils;

import com.aj.hyena.HyenaConstants;

public class TableNameHelper {

    public static String getPointTableName(String type) {
        return HyenaConstants.PREFIX_POINT_TABLE_NAME + type;
    }

    public static String getPointRecTableName(String type) {
        return HyenaConstants.PREFIX_POINT_TABLE_NAME + type + "_rec";
    }

    public static String getPointRecLogTableName(String type) {
        return HyenaConstants.PREFIX_POINT_TABLE_NAME + type + "_rec_log";
    }
}
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
export enum ApplicationEventTypes {
    BEFORE_TOPIC_DATA_LOADED = 'topic:before-topic-data-loaded',
    TOPIC_DATA_LOADED = 'topic:topic-data-loaded',

    BEFORE_TOPIC_DELETED = 'topic:before-topic-deleted',
    TOPIC_DELETED = 'topic:topic-deleted',
    TOPIC_ADDED = 'topic:topic-added',
    BEFORE_TOPIC_ADDED = 'topic:before-topic-added',

    TOPIC_EDITED = 'topic:topic-edited',
    BEFORE_TOPIC_EDITED = 'topic:before-topic-edited',

    BEFORE_CLUSTER_INFORMATION_DATA_LOADED = 'cluster-information:before-cluster-information-data-loaded',
    CLUSTER_INFORMATION_DATA_LOADED = 'cluster-information:cluster-information-data-loaded',

    BEFORE_TOPIC_CONSUMER_DATA_LOADED = 'topic:before-topic-consumer-data-loaded',
    TOPIC_CONSUMER_DATA_LOADED = 'topic:topic-consumer-data-loaded',

    BEFORE_CONSUMER_GROUP_DATA_LOADED = 'consumer-group:before-consumer-group-data-loaded',
    CONSUMER_GROUP_DATA_LOADED = 'consumer-group:consumer-group-data-loaded',

    DIALOG_CLOSE = 'dialog:close',
    DIALOG_OPEN = 'dialog:open',

    BEFORE_SCHEMA_DATA_LOADED = 'schema:before-schema-data-loaded',
    SCHEMA_DATA_LOADED = 'schema:schema-data-loaded',

    BEFORE_SCHEMA_COMPATIBILITY_DATA_LOADED = 'schema:before-schema-compatibility-data-loaded',
    SCHEMA_COMPATIBILITY_DATA_LOADED = 'schema:schema-compatibility-data-loaded',

    BEFORE_SUBJECT_VERSIONS_LIST_LOADED = 'schema:before-subject-version-list-loaded',
    SUBJECT_VERSIONS_LIST_LOADED = 'schema:subject-version-list-loaded',

    BEFORE_SUBJECTS_BY_TYPE_LOADED = 'subject:before-subjects-by-type-loaded',
    SUBJECTS_BY_TYPE_LOADED = 'subject:subjects-by-type-loaded',

    BEFORE_SCHEMA_COMPATIBILITY_UPDATE = 'config:before-schema-compatibility-update',
    SCHEMA_COMPATIBILITY_UPDATE = 'config:schema-compatibility-update',

    BEFORE_SCHEMA_VERSION_DELETE = 'schema:before-schema-version-delete',
    SCHEMA_VERSION_DELETE = 'schema:schema-version-delete',

    BEFORE_SCHEMA_SAVE = 'schema:before-schema-save',
    SCHEMA_SAVE = 'schema:schema-save',

    BEFORE_SCHEMA_READY = 'schema:before-schema-ready',
    SCHEMA_READY = 'schema:schema-ready',

    APPLICATION_ACTIVATED = 'application:activated',
    APPLICATION_DEACTIVATED = 'application:deactivated',

    BEFORE_SUBJECT_DELETE = 'subject:before-subject-delete',
    SUBJECT_DELETE = 'subject:before-subject-delete',

    MESSAGE_SELECTED = 'message:selected',
    MESSAGE_UNSELECTED = 'message:unselected',
}

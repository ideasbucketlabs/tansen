/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.exception;

public class TopicOperationException extends Exception {

    private final String proposedTopicName;

    public TopicOperationException(String proposedTopicName, String message, Throwable cause) {
        super(message, cause);
        this.proposedTopicName = proposedTopicName;
    }

    public String getProposedTopicName() {
        return proposedTopicName;
    }
}

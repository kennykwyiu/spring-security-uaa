package com.kenny.uaa.exception;

import com.kenny.uaa.util.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class UserNotEnabledProblem extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/user-not-enabled");

    public UserNotEnabledProblem() {
        super(
                TYPE,
                "Unauthorized Access",
                Status.UNAUTHORIZED,
                "User not enabled, please contact the administrator"
        );
    }
}

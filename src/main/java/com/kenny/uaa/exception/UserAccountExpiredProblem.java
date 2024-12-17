package com.kenny.uaa.exception;

import com.kenny.uaa.util.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class UserAccountExpiredProblem extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/user-locked");

    public UserAccountExpiredProblem() {
        super(
                TYPE,
                "Unauthorized Access",
                Status.UNAUTHORIZED,
                "User account has expired, please contact the administrator"
        );
    }
}

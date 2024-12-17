package com.kenny.uaa.exception;

import com.kenny.uaa.util.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class UserAccountLockedProblem extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/user-locked");

    public UserAccountLockedProblem() {
        super(
                TYPE,
                "Unauthorized Access",
                Status.UNAUTHORIZED,
                "User account is locked, please contact the administrator"
        );
    }
}

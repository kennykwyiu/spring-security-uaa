package com.kenny.uaa.exception;

import com.kenny.uaa.util.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class InvalidTotpProblem extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/invalid-token");

    public InvalidTotpProblem() {
        super(
                TYPE,
                "Invalid Verification Code",
                Status.UNAUTHORIZED,
                "The verification code is incorrect or has expired, please re-enter"
        );
    }
}

package com.lemon.soa.common.concurrent.support;

@FunctionalInterface
public interface DoubleBinaryOperator {

    /**
     * Applies this operator to the given operands.
     *
     * @param left the first operand
     * @param right the second operand
     * @return the operator result
     */
    double applyAsDouble(double left, double right);

}

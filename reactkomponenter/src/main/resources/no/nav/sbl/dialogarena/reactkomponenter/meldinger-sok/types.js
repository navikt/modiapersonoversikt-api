import PT from 'prop-types';

export const checkboxProps =  PT.shape({
    visCheckbox: PT.bool,
    checkBoxAction: PT.func,
    checkedBoxes: PT.array
});

export const submitButtonProps = PT.shape({
    buttonText: PT.string,
    errorMessage: PT.string,
    error: PT.bool
});
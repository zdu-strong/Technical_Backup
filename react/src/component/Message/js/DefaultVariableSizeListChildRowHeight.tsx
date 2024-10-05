/**
 * The height of each message has a default value, and the height of each line must not be lower than the default value.
 * Based on the first article, the default height must be greater than or equal to the height of withdrawal, deletion, etc.
 * In order to ensure that people who have read the message will not have doubts about whether to pass it, it is not allowed to modify it after it is sent.
 * Images, etc. must be resources to set the maximum width and height. Since the loading time of images, etc. is uncertain, they can be smaller, but cannot be larger
 */
export const DefaultVariableSizeListChildRowHeight = 100;
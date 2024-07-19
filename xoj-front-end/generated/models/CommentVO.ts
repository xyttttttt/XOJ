import { UserVO } from "./UserVO";

export type CommentVO = {
  id?: number;
  isDelete?: number;
  questionId?: number;
  comment?: string;
  favourNum?: number;
  parentCommentId?: number;
  userId?: number;
  thumbNum?: number;
  userVO?: UserVO;
  createTime?: string;
  like: boolean;
};

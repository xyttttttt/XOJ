/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */

import { OrderItem } from "./OrderItem";
import { CommentVO } from "./CommentVO";

export type Page_CommentVO_ = {
  countId?: string;
  current?: number;
  maxLimit?: number;
  optimizeCountSql?: boolean;
  // @ts-ignore
  orders?: Array<OrderItem>;
  pages?: number;
  // @ts-ignore
  records?: Array<CommentVO>;
  searchCount?: boolean;
  size?: number;
  total?: number;
};

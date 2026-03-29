#!/bin/bash

set -e

BUCKET_NAME="${LOCAL_S3_BUCKET_NAME:-qootalk-s3-local}"

if ! awslocal s3 ls "s3://${BUCKET_NAME}" >/dev/null 2>&1; then
  awslocal s3 mb "s3://${BUCKET_NAME}"
fi

<?php

namespace App\Utils;

trait Result
{
    public function success()
    {
        return response()->json([
            'status' => 'success',
            'status_code' => 1000
        ], 200);
    }

    public function successWithData($data)
    {
        return response()->json([
            'status' => 'success',
            'status_code' => 1000,
            'data' => $data
        ], 200);
    }

    public function successWithPage($vo)
    {
        $data = array(
            'total'=>$vo->total(),
            'page_size'=>$vo->perPage(),
            'page'=>$vo->currentPage(),
            'page_count'=>$vo->lastPage(),
            'items'=>$vo->toArray()['data']
        );

        return $this->successWithData($data);
    }

    public function errorWithCodeAndInfo($code, $message)
    {
        return response()->json([
            'status' => 'error',
            'status_code' => $code,
            'message' => $message
        ], 200);
    }

    public function validateError($message)
    {
        return $this->errorWithCodeAndInfo(1001, $message);
    }

    public function errorWithInfo($message)
    {
        return $this->errorWithCodeAndInfo(1002, $message);
    }
}

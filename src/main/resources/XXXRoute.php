Route::post('/v1/@@@table', 'Backend\XXXController@add');
Route::put('/v1/@@@table/{id}', 'Backend\XXXController@update');
Route::put('/v1/@@@tables', 'Backend\XXXController@batchUpdate');
Route::delete('/v1/@@@table/{id}', 'Backend\XXXController@delete');
Route::get('/v1/@@@table/{id}', 'Backend\XXXController@queryById');
Route::get('/v1/@@@table', 'Backend\XXXController@queryList');
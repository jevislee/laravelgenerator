Route::post('/v1/@@@table', 'XXXController@add');
Route::put('/v1/@@@table/{id}', 'XXXController@update');
Route::delete('/v1/@@@table/{id}', 'XXXController@delete');
Route::get('/v1/@@@table/{id}', 'XXXController@queryById');
Route::get('/v1/@@@table', 'XXXController@queryList');